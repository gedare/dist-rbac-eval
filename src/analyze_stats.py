#
# analyze_stats.py
#
# Analyze results from running benchmark experiments
#

import math
import sys
import getopt
import array
import os
import copy
import string
import numpy
import scipy
from scipy import stats

def usage():
  print "\
Usage: python analyze_stats.py -[ho:]\n\
  -h --help           print this help\n\
  -o --output=        output directory [NONE]\n\
  -i --input=         input directory containing CBF files [NONE]\n\
  -t --tags=          tags for experiment, comma-separated if more than one,\n\
                      and each must be a subdirectory of input [rbac_inter]\n\
"

##
# Utility functions
##
def grep(filename, pattern):
  result = [];
  file = open(filename,'r')
  for line in file.readlines():
    if re.match(".*"+pattern+".*", line):
      result.append(string.strip(line))
  file.close()
  return result

##
# gnuplot helpers
##
def reset(gp):
  gp.write("reset\n")

def create_lineplots_header():
  return"\
set terminal postscript eps enhanced solid color lw 1 \"Times-Roman\" 18\n\
set key outside right\n\
"

###
# Results processing
###

# index_by_name[] is a dictionary that maps mnemonics for fields of "rows" of
# results to the "column" index for that mnemonic/field. So for example the
# first column contains a high-level tag for the experiment (e.g. rbac_inter
# or rbac_intra), so the key "tag" maps to 0 (since the columns are 0-indexed)
index_by_name=dict(tag=0, depth_of_rh=1, nature_of_rh=2, roles=3,
    permissions=4, sessions=5, algorithm=6,
    atmean=7, atstd=8, atmin=9, atq1=10, atmed=11, atq3=12, atmax=13,
    itmean=14, itstd=15, itmin=16, itq1=17, itmed=18, itq3=19, itmax=20,
    dtmean=21, dtstd=22, dtmin=23, dtq1=24, dtmed=25, dtq3=26, dtmax=27
    ) #FIXME

# return the value in the field with given name for a single row of the results
def get_field(result,name):
  return result[index_by_name[name]]

# construct a list of all the results whose field_name matches the value
# this is used for example to reduce the results by matching a given field
# such as filter_field_by_value(results, tag, 'rbac_inter') to get only the
# results for the experiments tagged as rbac_inter.
def filter_results_by_field_value(results, field_name, value):
  reduce_results = []
  for r in results:
    if get_field(r, field_name) == str(value):
      reduce_results.append(r)
  return reduce_results

##
# CBF file processing
##

# get_results_from_CBF() reads the results from the CBF file named filename.
def get_results_from_CBF(filename, tag):
  results = []
  results.append(tag)

  metadata = os.path.split(filename)[1].split('.')

  # the CBF file name encodes information about the run (if the run_XXX.expts
  # script is used. The file name is organized as follows:
  # if tag is 'rbac_intra':
  #   _stats.${Depth_of_RH}.${Nature_of_RH}.${R}.${P}.${algnum}.CBF
  # elif tag is 'rbac_inter':
  #   _stats.${Depth_of_RH}.${Nature_of_RH}.${sessions}.${algnum}.CBF
  # else:
  #   Unknown
  if tag == 'rbac_intra':
    results.append(metadata[1])
    results.append(metadata[2])
    results.append(metadata[3])
    results.append(metadata[4])
    results.append("8") # FIXME: hard-coded for KTZ
    results.append(metadata[5])
  elif tag == 'rbac_inter':
    results.append(metadata[1])
    results.append(metadata[2])
    results.append("100") # FIXME: hard-coded for KTZ
    results.append("250") # FIXME: hard-coded for KTZ
    results.append(metadata[3])
    results.append(metadata[4])
    pass
  else:
    assert False, "Unknown tag: " + tag
    usage()
    sys.exit(2)

  f = open(filename)
  arr = numpy.array([float(line.strip().split(' ')[0]) / 1000.0 for line in f])
  #results.append(numpy.mean(arr)) # atmean
  #results.append(numpy.std(arr))  # atstd
  #for p in [0, 25, 50, 75, 100]:
  #  results.append(scipy.stats.scoreatpercentile(arr, p)) # min, q1, med, q3, max
  f.close()
  return results

def get_results_from_raw_txt(filename, tag):
  results = []
  basename, extension = os.path.splitext(filename)
  rawtimefile = basename + ".txt"
  if not os.path.exists(rawtimefile):
    return []
  f = open(rawtimefile)

  data = []
  times = []
  for line in f:
    l = line.strip().split()
    if not l:
      continue
    if len(l) == 1:
      stop_index = int(l[0]) + 1
      start_index = stop_index - 5
      times.extend(data[start_index:stop_index])
      data = []
    else:
      data.append(l)

  if not times:
    return [0.0, 0.0, 0.0, 0.0]

  init_times = [float(t[0])/1000.0 for t in times]
  access_times = [float(t[1])/1000.0 for t in times]
  destroy_times = [float(t[2])/1000.0 for t in times]

  arr = numpy.array(access_times)
  results.append(numpy.mean(arr))
  results.append(numpy.std(arr))
  for p in [0, 25, 50, 75, 100]:
    results.append(scipy.stats.scoreatpercentile(arr, p)) # min, q1, med, q3, max

  arr = numpy.array(init_times)
  results.append(numpy.mean(arr))
  results.append(numpy.std(arr))
  for p in [0, 25, 50, 75, 100]:
    results.append(scipy.stats.scoreatpercentile(arr, p)) # min, q1, med, q3, max



  arr = numpy.array(destroy_times)
  results.append(numpy.mean(arr))
  results.append(numpy.std(arr))
  for p in [0, 25, 50, 75, 100]:
    results.append(scipy.stats.scoreatpercentile(arr, p)) # min, q1, med, q3, max

  return results

# construct a list of lists containing the results from all the files given
def get_results_from_CBF_files(filenames, tag):
  results = []
  for f in filenames:
    r = get_results_from_CBF(f, tag)
    r.extend(get_results_from_raw_txt(f, tag))
    results.append(r)
  return results

# return a list containing input/tag/*.CBF
def get_CBF_files(input, tag):
  tagpath = os.path.join(input, tag)
  files = [
      os.path.join(tagpath, f) for f in os.listdir(tagpath)
      if os.path.isfile(os.path.join(tagpath, f))
      and string.find(f,"CBF") != -1
  ]
  return files

def create_header(xmin, xmax, xlabel, ylabel, title, outfile):
  return"\
set terminal postscript eps enhanced solid color lw 1 \"Times-Roman\" 18\n\
set key outside right\n\
set output \"" + os.path.splitext(os.path.split(outfile)[1])[0] + ".eps\"\n\
set xlabel '" + xlabel + "' font \"Times-Roman,18\"\n\
set ylabel '" + ylabel + "' font \"Times-Roman,18\"\n\
set title \"" + title + "\" font \"Times-Roman,20\"\n\
set xrange [" + xmin + ":" + xmax + "] noreverse nowriteback\n\
set style line 1 lt rgb \"red\" lw 3 pt 6\n\
set style line 2 lt rgb \"blue\" lw 3 pt 6\n\
"



def create_lineplots_header(xmin, xmax, xlabel, ylabel, title, outfile):
  return create_header(xmin, xmax, xlabel, ylabel, title, outfile)

def create_boxplots_header(xmin, xmax, xlabel, ylabel, title, outfile):
  return create_header(xmin, xmax, xlabel, ylabel, title, outfile) + "\
set bars 2.0\n\
set style fill empty\n\
"


# type can be:
#   line
# datasets is a list of lists that contain a name and data to plot.
#   data is of the form:
#     [xcoord, mean, std, min, q1, med, q3, max]
def create_plot(type, datasets, xmin, xmax, xlabel, ylabel, title, outfile):

  dataset_index = dict([("x", 0), ("mean",1), ("std",2),
    ("min",3), ("q1",4), ("med",5), ("q3",6), ("max",7)])
  gp = open(outfile, 'w')
  reset(gp)

  if 'line' in type:
    header = create_lineplots_header(xmin, xmax, xlabel, ylabel, title, outfile)
    plotcmd = "plot "
    i = 1
    data = ""
    for dataset in datasets:
      lp = "'-' title \"" + dataset[0] + "\" with linespoints ls " + str(i)
      lp = lp + ",'-' notitle with errorbars ls " + str(i)
      plotcmd = plotcmd + lp
      for d in dataset[1:]:
        data = data + d[dataset_index["x"]] + " " + d[dataset_index["mean"]] + " " + d[dataset_index["std"]] + "\n"
      # Repeat data for error lines
      data = data + "e\n"
      for d in dataset[1:]:
        data = data + d[dataset_index["x"]] + " " + d[dataset_index["mean"]] + " " + d[dataset_index["std"]] + "\n"
      if i < len(datasets):
        plotcmd = plotcmd + ","
        data = data + "e\n"
      i = i + 1
    plotcmd = plotcmd + "\n"
    gp.write(header + plotcmd + data)

  if 'box' in type:
    header = create_boxplots_header(str(int(xmin)/2), str(int(xmax)+int(xmin)), xlabel, ylabel, title, outfile)
    plotcmd = "plot "
    i = 1
    data = ""
    for dataset in datasets:
      bp =  "'-' with candlesticks title \"" + dataset[0] + "\" lt " + str(i)
      bp = bp + ",'-' notitle with candlesticks lt -1"
      plotcmd = plotcmd + bp
      for d in dataset[1:]:
        data = data + d[dataset_index["x"]] + " " + d[dataset_index["q1"]] + " " + d[dataset_index["min"]] + " " + d[dataset_index["max"]] + " "+ d[dataset_index["q3"]] + "\n"
      # now add data for median
      data = data + "e\n"
      for d in dataset[1:]:
        data = data + d[dataset_index["x"]] + " " + d[dataset_index["med"]] + " " + d[dataset_index["med"]] + " " + d[dataset_index["med"]] + " " + d[dataset_index["med"]] + "\n"
      if i < len(datasets):
        plotcmd = plotcmd + ","
        data = data + "e\n"
      i = i + 1
    plotcmd = plotcmd + "\n"
    gp.write(header + plotcmd + data)
  gp.close()

##
# Analysis
##
def extract_dataset(results, x, mean, std, min, q1, med, q3, max):
  dataset = []
  if not results:
    return [], [], 0
  xmin = int(get_field(results[0], x))
  xmax = xmin
  for r in results:
    data = []
    rx = int(get_field(r,x))
    if rx < xmin:
      xmin = rx
    if rx > xmax:
      xmax = rx
    if len(r) > index_by_name[mean]:
      data.append(str(rx))
      data.append(str(get_field(r, mean)))
      data.append(str(get_field(r, std)))
      data.append(str(get_field(r, min)))
      data.append(str(get_field(r, q1)))
      data.append(str(get_field(r, med)))
      data.append(str(get_field(r, q3)))
      data.append(str(get_field(r, max)))
      dataset.append(data)
  if dataset: # sort by numerical value of x if data exists
    dataset.sort(key=lambda xcoord: int(xcoord[0]))
  return dataset, xmin, xmax

def analyze_it(results, tag, output):

  name_from_type = dict([("at","Access Check"),
    ("it","Initialize Session"), ("dt","Destroy Session")])

  name_from_algorithm = dict([(0,"Directed Graph"),
    (1,"Access Matrix"), (2,"Authorization Recycling"), (3, "CPOL"),
    (4, "Bloom Filter"), (5, "Map BitSet"),
    (6, "HWDS BitSet"), (7, "HWDS Multimap")])

  if tag == 'rbac_inter':
    r = [filter_results_by_field_value(results, 'algorithm', algorithm)
        for algorithm in xrange(8)]
    s = []
    h = []
    c = []
    for x in xrange(8):
      s.append(filter_results_by_field_value(r[x], 'nature_of_rh', 0))
      h.append(filter_results_by_field_value(r[x], 'nature_of_rh', 1))
      c.append(filter_results_by_field_value(h[x], 'depth_of_rh', 1))
    for x in xrange(8):
      if len(r[x]) == 0:
          continue
      for t in ["at", "it", "dt"]:
        plot_data = []
        boxplot_data = []
        minima = []
        maxima = []
        for l, d in [("Stanford", s[x]), ("Hybrid", h[x]), ("Core", c[x])]:
          data, m, M = extract_dataset(d, 'sessions', t+'mean', t+'std',
              t+'min', t+'q1', t+'med', t+'q3', t+'max')
          if len(data) != 0:
            labelled_data = [l]
            labelled_data.extend(data)
            plot_data.append(labelled_data)
            minima.append(m)
            maxima.append(M)
        if len(plot_data) == 0:
          continue
        xmin = min(minima)
        xmax = max(maxima)
        for plottype in ['line', 'box']:
          create_plot(plottype, plot_data, str(xmin), str(xmax), "# Sessions",
            name_from_type[t] + " Time (us)", name_from_algorithm[x],
            os.path.join(output, "inter" + t + str(x) + "_" + plottype + ".p"))

    for t in ["at", "it", "dt"]:
      for l, d in [("Stanford", s), ("Hybrid", h), ("Core", c)]:
        plot_data = []
        minima = []
        maxima = []
        for x in xrange(8):
          if len(r[x]) == 0:
            continue
          data, m, M = extract_dataset(d[x], 'sessions', t+'mean', t+'std',
              t+'min', t+'q1', t+'med', t+'q3', t+'max')
          if len(data) != 0:
            labelled_data = [name_from_algorithm[x]]
            labelled_data.extend(data)
            plot_data.append(labelled_data)
            minima.append(m)
            maxima.append(M)
        if len(plot_data) == 0:
          continue
        xmin = min(minima)
        xmax = max(maxima)
        for plottype in ['line', 'box']:
          create_plot(plottype, plot_data, str(xmin), str(xmax), "# Sessions",
            name_from_type[t] + " Time (us)", l,
            os.path.join(output, "inter" + t + l + "_" + plottype + ".p")) 

  elif tag == 'rbac_intra':
    r = filter_results_by_field_value(results, 'permissions', '250')
    p = filter_results_by_field_value(results, 'roles', '100')

    role_results = [filter_results_by_field_value(r, 'algorithm', x)
        for x in xrange(8)]
    perm_results = [filter_results_by_field_value(p, 'algorithm', x)
        for x in xrange(8)]

    for t in ["at", "it", "dt"]:
      rd = []
      rxminima = []
      rxmaxima = []
      pd = []
      pxminima = []
      pxmaxima = []
      for x in xrange(8):
        a, b, c = extract_dataset(role_results[x], 'roles', t+'mean', t+'std',
              t+'min', t+'q1', t+'med', t+'q3', t+'max')
        if len(a) != 0:
          labelled_data = [name_from_algorithm[x]]
          labelled_data.extend(a)
          rd.append(labelled_data)
          rxminima.append(b)
          rxmaxima.append(c)

        a, b, c = extract_dataset(
            perm_results[x], 'permissions', t+'mean', t+'std',
            t+'min', t+'q1', t+'med', t+'q3', t+'max')
        if len(a) != 0:
          labelled_data = [name_from_algorithm[x]]
          labelled_data.extend(a)
          pd.append(labelled_data)
          pxminima.append(b)
          pxmaxima.append(c)

      if rd:
        rxmin = min(rxminima)
        rxmax = max(rxmaxima)
        for plottype in ['line', 'box']:
          create_plot(plottype, rd, str(rxmin), str(rxmax), "# Roles",
            name_from_type[t] + " Time (us)", "Intra-session Roles",
            os.path.join(output, "intra_roles_" + t + "_" + plottype + ".p"))
   
      if pd:
        pxmin = min(pxminima)
        pxmax = max(pxmaxima)
        for plottype in ['line', 'box']:
          create_plot(plottype, pd, str(pxmin), str(pxmax), "# Permissions",
            name_from_type[t] + " Time (us)", "Intra-session Permissions",
            os.path.join(output, "intra_perms_" + t + "_" + plottype + ".p"))

  else: # this shouldn't happen. 
    assert False, "Unknown tag: " + tag
    sys.exit(2)


##
# Script entry point
##
def main():
  # default args
  output = ""
  input = ""
  tags = ["rbac_inter"]

  # Process command line args
  try:
    opts, args = getopt.getopt(sys.argv[1:], "ho:i:t:",
        ["help", "output=", "input=", "tag="]
    )
  except getopt.GetoptError, err:
    print str(err)
    usage()
    sys.exit(2)
  for opt, arg in opts:
    if opt in ("-h", "--help"):
      usage()
      sys.exit()
    elif opt in ("-o", "--output"):
      output = arg
    elif opt in ("-i", "--input"):
      input = arg
    elif opt in ("-t", "--tag"):
      tags = arg.split(',')
    else:
      assert False, "unhandled option: " + opt

  # verify args
  if not os.path.exists(output):
    print("Invalid output directory " + output)
    usage()
    sys.exit(2)
  if not os.path.exists(input):
    print("Invalid input directory " + input)
    usage()
    sys.exit(2)
  for tag in tags:
    if not os.path.exists(os.path.join(input,tag)):
      print("Invalid tag directory " + os.path.join(input,tag))
      usage()
      sys.exit(2)

  # do it.
  for tag in tags:
    print("Analyzing: " + input + ":" + tag + " -> " + output)
    files = get_CBF_files(input, tag)
    results = get_results_from_CBF_files(files, tag)
    analyze_it(results, tag, output)

# the actual entry point.
if __name__ == "__main__":
  main()
