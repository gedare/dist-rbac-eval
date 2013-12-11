#!/usr/bin/env python

import getopt
import sys
import os
import experiment

def usage():
  print "\
Usage: python run_experiments.py -[hb:e:]\n\
  -h --help           print this help\n\
  -b --base=          set base directory of dist-rbac-eval [cwd]\n\
  -e --experiments=   comma-separated list of experiments to run, including\n\
                      KTZ_inter: KTZ inter-session experiments [default]\n\
"

def do_KTZ_inter(base):
  inter = experiment.InterSessionExperiment(base, 'dataset_KTZ', 'stats_KTZ')
  sessions = [x for x in range(2, 16)] # 2--15
  algorithms = [x for x in range(0, 4)] # 0--3
  inter.run_experiments(sessions, algorithms)

def main():
  # default args
  base = os.getcwd()
  experiments = ['KTZ_inter']

  # Process command line args
  try:
    opts, args = getopt.getopt(sys.argv[1:], "hb:e:",
        ["help", "base=", "experiments="]
    )
  except getopt.GetoptError, err:
    print str(err)
    usage()
    sys.exit(2)
  for opt, arg in opts:
    if opt in ("-h", "--help"):
      usage()
      sys.exit()
    elif opt in ("-b", "--base"):
      base = arg
    elif opt in ("-e", "--experiments"):
      experiments = arg.split(',')
    else:
      assert False, "unhandled option: " + opt

  dispatch_map = { 'KTZ_inter' : do_KTZ_inter}
  for x in experiments:
    try:
      func = dispatch_map[x]
      func(base)
    except KeyError:
      raise ValueError('Invalid experiment' + x)

# script entry point
if __name__ == "__main__":
  main()
