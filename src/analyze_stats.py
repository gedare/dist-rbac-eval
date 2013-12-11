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
index_by_name=dict(tag=0, depth_of_rh=1, nature_of_rh=2, roles=3, permissions=4,
    sessions=5, algorithm=6, mean=7, std=8) #FIXME

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

  metadata = filename.split('.')

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
    results.append("16") # FIXME: hard-coded for KTZ
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
  arr = numpy.array([ float(line.strip()) / 1000.0 for line in f ])
  results.append(numpy.mean(arr))
  results.append(numpy.std(arr))
  f.close()
  assert len(index_by_name) == len(results)
  return results

# construct a list of lists containing the results from all the files given
def get_results_from_CBF_files(filenames, tag):
  results = []
  for f in filenames:
    results.append(get_results_from_CBF(f, tag))
  return results

# return a list containing input/tag/*.CBF
def get_CBF_files(input, tag):
  tagpath = os.path.join(input, tag)
  files = [
      os.path.join(tagpath, f) for f in os.listdir(tagpath)
      if os.path.isfile(os.path.join(tagpath, f))
  ]
  # filter out any files that aren't CBF
  for f in files:
    if string.find(f, "CBF") == -1:
      files.remove(f)
  return files

def create_lineplots_header(xmin, xmax, xlabel, ylabel, title, outfile):
  return"\
set terminal postscript eps enhanced solid color lw 1 \"Times-Roman\" 18\n\
set key outside right\n\
set xlabel '" + xlabel + "' font \"Times-Roman,18\"\n\
set ylabel '" + ylabel + "' font \"Times-Roman,18\"\n\
set output \"" + os.path.splitext(os.path.split(outfile)[1])[0] + ".eps\"\n\
set title \"" + title + "\" font \"Times-Roman,20\"\n\
set xrange [" + xmin + ":" + xmax + "] noreverse nowriteback\n\
set style line 1 lt rgb \"red\" lw 3 pt 6\n\
set style line 2 lt rgb \"blue\" lw 3 pt 6\n\
"

# type can be:
#   line
# datasets is a list of lists that contain a name and data to plot.
#   data is of the form:
#     [xcoord, yvalue], or 
#     [xcoord, yvalue, err], or 
#     [xcoord, yvalue, +err, -err]
def create_plot(type, datasets, xmin, xmax, xlabel, ylabel, title, outfile):
  if type == 'line':
    header = create_lineplots_header(xmin, xmax, xlabel, ylabel, title, outfile)

    plotcmd = "plot "
    i = 1
    data = ""
    for dataset in datasets:
      lp = "'-' title \"" + dataset[0] + "\" with linespoints ls " + str(i)
      if len(dataset[1]) > 2:
        lp = lp + ",'-' title \"" + dataset[0] + " (std)\""
        lp = lp + " with errorbars ls " + str(i)
      plotcmd = plotcmd + lp
      for d in dataset[1:]:
        data = data + " ".join(d) + "\n"
      if len(dataset[1]) > 2:
        data = data + "e\n"
        for d in dataset[1:]:
          data = data + " ".join(d) + "\n"
      if i < len(datasets):
        plotcmd = plotcmd + ","
        data = data + "e\n"
      i = i + 1
    plotcmd = plotcmd + "\n"
    gp = open(outfile, 'w')
    reset(gp)
    gp.write(header + plotcmd + data)
    gp.close()

  else:
    assert False, "Unknown plot type: " + type

##
# Analysis
##
def extract_dataset(results, x, y, err):
  dataset = []
  xmin = float(get_field(results[0], x))
  xmax = xmin
  for r in results:
    data = []
    rx = float(get_field(r,x))
    if rx < xmin:
      xmin = rx
    if rx > xmax:
      xmax = rx
    data.append(str(rx))
    data.append(str(get_field(r, y)))
    data.append(str(get_field(r, err)))
    dataset.append(data)
  return dataset, xmin, xmax

def analyze_it(results, tag, output):
  if tag == 'rbac_inter':
    for algorithm in xrange(6):
      r = filter_results_by_field_value(results, 'algorithm', algorithm)
      if len(r) == 0:
        continue
      s = filter_results_by_field_value(r, 'nature_of_rh', 0)
      h = filter_results_by_field_value(r, 'nature_of_rh', 1)
      c = filter_results_by_field_value(h, 'depth_of_rh', 1)
      sd, sxmin, sxmax = extract_dataset(s, 'sessions', 'mean', 'std')
      hd, hxmin, hxmax = extract_dataset(h, 'sessions', 'mean', 'std')
      cd, cxmin, cxmax = extract_dataset(c, 'sessions', 'mean', 'std')
      xmin = sxmin
      xmax = sxmax
      Stanford = ["Stanford"]
      Stanford.extend(sd)
      Hybrid = ["Hybrid"]
      Hybrid.extend(hd)
      Core = ["Core"]
      Core.extend(cd)
      create_plot('line', [Stanford, Core], str(xmin), str(xmax),
          "# Sessions", "Access check time (us)", str(algorithm),
          os.path.join(output, "inter" + str(algorithm) + ".p"))


  elif tag == 'rbac_intra':
    pass
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
