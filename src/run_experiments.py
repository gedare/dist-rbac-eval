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
  -e --experiments=   comma-separated list of experiments to run [KTZ_inter]:\n\
                      KTZ_inter: KTZ inter-session experiments\n\
                      KTZ_intra: KTZ intra-session experiments\n\
  -a --algorithms=    comma-separated list of algorithms to run [0,1,3]:\n\
                        0: directed graph\n\
                        1: access matrix\n\
                        2: authorization recycling\n\
                        3: CPOL\n\
                        4: Bloom and Cascade Bloom filters (unimplemented)\n\
"

def do_KTZsmall_inter(base, algorithms):
  inter = experiment.InterSessionExperiment(base, 'dataset_KTZsubset',
      'stats_KTZsmall')
  sessions = [x for x in range(3, 5)] # 2--4
  rh = [ (0, [5]), (1, [1]) ]
  inter.run_experiments(sessions, rh, algorithms)

def do_KTZsmall_intra(base, algorithms):
  intra = experiment.IntraSessionExperiment(base, 'dataset_KTZsubset',
      'stats_KTZsmall')
  RP = []
  P = 250
  for R in [10,50,100]:
    RP.append((R,P))
  R = 100
  for P in [10,50,100]:
    RP.append((R,P))
  intra.run_experiments(RP, algorithms)

def do_KTZsubset_inter(base, algorithms):
  inter = experiment.InterSessionExperiment(base, 'dataset_KTZsubset',
      'stats_KTZsubset')
  sessions = [x for x in range(3, 9)] # 2--8
  rh = [ (0, [5]), (1, [1]) ]
  inter.run_experiments(sessions, rh, algorithms)

def do_KTZsubset_intra(base, algorithms):
  intra = experiment.IntraSessionExperiment(base, 'dataset_KTZsubset',
      'stats_KTZsubset')
  RP = []
  P = 250
  for R in [250,500,750,2000]:
    RP.append((R,P))
  R = 100
  for P in [100,500,700,2000]:
    RP.append((R,P))
  intra.run_experiments(RP, algorithms)

def do_KTZ_inter(base, algorithms):
  inter = experiment.InterSessionExperiment(base, 'dataset_KTZ', 'stats_KTZ')
  sessions = [x for x in range(3, 16)] # 2--15
  rh = [ (0, [5]), (1, [1]) , (1, [1,2,3,4,5]) ]
  inter.run_experiments(sessions, rh, algorithms)

def do_KTZ_intra(base, algorithms):
  intra = experiment.IntraSessionExperiment(base, 'dataset_KTZ', 'stats_KTZ')
  RP = []
  P = 250
  for R in [500,700,2000,3000,6000,8000,10000]:
    RP.append((R,P))
  R = 100
  for P in [100,500,700,2000,3000,6000,8000,10000]:
    RP.append((R,P))
  intra.run_experiments(RP, algorithms)

def main():
  # default args
  base = os.getcwd()
  experiments = ['KTZ_inter']
  algorithms = ['0', '1', '3']

  # Process command line args
  try:
    opts, args = getopt.getopt(sys.argv[1:], "hb:e:a:",
        ["help", "base=", "experiments=", "algorithms="]
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
    elif opt in ("-a", "--algorithms"):
      algorithms = arg.split(',')
    else:
      assert False, "unhandled option: " + opt

  dispatch_map = {
      'KTZ_inter' : do_KTZ_inter,
      'KTZ_intra' : do_KTZ_intra,
      'KTZsubset_inter' : do_KTZsubset_inter,
      'KTZsubset_intra' : do_KTZsubset_intra,
      'KTZsmall_inter' : do_KTZsmall_inter,
      'KTZsmall_intra' : do_KTZsmall_intra,
      }
  for x in experiments:
    try:
      func = dispatch_map[x]
    except KeyError:
      raise ValueError('Invalid experiment ' + x)
    func(base, algorithms)

# script entry point
if __name__ == "__main__":
  main()
