#!/usr/bin/env python

import os
import shutil
import subprocess

## Some utilities. I should put these in a module... some day.

##
# wc: a variant of the unix utility, returns a list containing the
#     number of lines, words, and bytes (characters) in the given filename
# FIXME: word and byte counts are not accurate / consistent with wc.
##
def wc(filename):
  lines = 0
  words = 0
  bytes = 0
  with open(filename) as f:
    for i, l in enumerate(f, 1):
      words = words + l.count(' ')
      if not l[len(l)-1] == ' ':
        words = words + 1 # detect word at end of line
      bytes = bytes + len(l) + 1 # include the newline character
      lines = lines + 1
  return [lines, words, bytes]


class Experiment(object):
  def __init__(self, base, input, output):
    assert os.path.isdir(base)
    self.basedir = str(base)

    self.inputdir = os.path.join(self.basedir, str(input))
    assert os.path.isdir(self.inputdir)

    self.outputdir = os.path.join(self.basedir, str(output))
    # The arguments should be sanitized before touching the filesystem
    if not os.path.exists(self.outputdir):
      os.mkdirs(self.outputdir)
    assert os.path.isdir(self.outputdir)

    self.datadir = os.path.join(self.basedir, 'Data')
    assert os.path.isdir(self.datadir)

  def run_instance(self, algnum, optionalargs, loopcnt, CBF_filename):
    means = os.path.join(self.datadir, 'means')
    if not os.path.exists(means):
      os.mknod(means)
    
    simcmd = []
    simcmd.append('java')
    simcmd.append('-Xms1500M')
    simcmd.append('-Xmx1500M')
    if int(algnum) == 2:
      simcmd.append('Simulate.Simulate2')
    else:
      simcmd.append('Simulate.Simulate3')
    simcmd.append(os.path.join(self.datadir, 'rbac-state'))
    simcmd.append(str(algnum))

    if optionalargs:
      simcmd.extend(optionalargs)
    
    measurecmd = ['java', 'Simulate.Measurements']
    livelock_limit = 100
    success = False
    for i in range(livelock_limit):
      simproc = subprocess.call(simcmd)
      measureproc = subprocess.call(measurecmd)
      if wc(means)[0] >= loopcnt:
        success = True
        break
    if not success:
      print "Livelock detected, continuing anyway"
    os.rename(means, os.path.join(self.outputdir, CBF_filename))

class InterSessionExperiment(Experiment):
  def __init__(self, base, input, output):
    super(InterSessionExperiment, self).__init__(base, input, output)
    self.inputdir = os.path.join(self.inputdir, "rbac_inter")
    self.outputdir = os.path.join(self.outputdir, "rbac_inter")
    print self.inputdir

  def run_experiments(self, sessions, algorithms):
    rh = [ (0, [5]), (1, [1]) , (1, [1,2,3,4,5]) ]
    for s in sessions:
      print("Sessions: " + str(s))
      for nature_of_rh, depth_of_rh in rh:
        n = str(nature_of_rh)
        for depth in depth_of_rh:
          d = str(depth)
          expdir = os.path.join(self.inputdir, d + "_" + n)
          assert os.path.exists(expdir)
          insn = os.path.join(expdir, "instructions_" + str(s) + ".3")
          assert os.path.isfile(insn)
          rbac = os.path.join(expdir, "rbac-state")
          assert os.path.isfile(rbac)
          shutil.copyfile(os.path.join(self.inputdir, insn),
              os.path.join(self.datadir, 'instructions'))
          shutil.copy(os.path.join(self.inputdir, rbac), self.datadir)
          for a in algorithms:
            filename = os.path.join(self.outputdir,
              "_stats." + d + "." + n + "." + str(s) + "." + str(a) + ".CBF")
            super(InterSessionExperiment, self).run_instance(a, [], 5, filename)

