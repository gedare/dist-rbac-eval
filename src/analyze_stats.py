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
    sessions=5, algorithm=6, list_of_mean_times=7) #FIXME

# return the value in the field with given name for a single row of the results
def get_field(result,name):
  return result[index_by_name[name]]

# construct a list of all the results whose field_name matches the value
# this is used for example to reduce the results by matching a given field
# such as filter_field_by_value(results, tag, 'rbac_inter') to get only the
# results for the experiments tagged as rbac_inter.
def filter_field_by_value(results, field_name, value):
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
  results.append([line.strip() for line in f])
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

##
# Analysis
##
def analyze_it(input, output, tags):
  for tag in tags:
    print("Analyzing: " + input + ":" + tag + " -> " + output)
    files = get_CBF_files(input, tag)
    results = get_results_from_CBF_files(files, tag)
    # TODO: process the results.



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

  # TODO: do it.
  analyze_it(input, output, tags)

# the actual entry point.
if __name__ == "__main__":
  main()
