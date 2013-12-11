#!/usr/bin/env python

import os
import shutil

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
  lines = i
  return [lines, words, bytes]


class Experiment:
  def __init__(self, basedir, indir, outdir):
    self.basedir = str(basedir)
    self.outdir = str(outdir)
    self.indir = str(indir)
    assert os.path.isdir(basedir)
    assert os.path.isdir(indir)

    # The arguments should be sanitized before touching the filesystem
    if not os.path.exists(outdir):
      os.mkdirs(outdir)
    assert os.path.isdir(outdir)

    self.datadir = os.path.join(basedir, 'Data')
    assert os.path.isdir(self.datadir)

  def run_instance(instructions, rbac_state, algorithms,
      optionalargs, loopcnt, CBF_filename):
    assert os.path.isfile(instructions)
    assert os.path.isfile(rbac_state)
    
    # The arguments should be sanitized before touching the filesystem
    shutil.copyfile(os.path.join(indir, instructions),
        os.path.join(datadir, 'instructions'))
    shutil.copy(os.path.join(indir, rbac_state), datadir)
    for algnum in algorithms:
      means = os.path.join(datadir, 'means')
      if not os.path.exists(means):
        os.path.mknod(means)
      
      simcmd = []
      simcmd.append('java')
      simcmd.append('-Xms1500M')
      simcmd.append('-Xmx1500M')
      if int(algnum) == 2:
        simcmd.append('Simulate.Simulate2')
      else:
        simcmd.append('Simulate.Simulate3')
      simcmd.append(os.path.join(datadir, 'rbac-state'))
      simcmd.append(algnum)

      if optionalargs:
        simcmd.extend(optionalargs)
      
      measurecmd = ['java', 'Simulate.Measurements']

      livelock_limit = 100
      for i in xrange(livelock_limit):
        simproc = subprocess.Popen(simcmd, stdout=subprocess.PIPE, shell=True)
        measureproc = subprocess.Popen(measurecmd,
            stdout=subprocess.PIPE, shell=True)
        (m1, e1) = simproc.communicate() # return ignored..
        (m2, e2) = measureproc.communicate() # return ignored..
        if wc(means)[0] > 4:
          break
      os.rename(means, os.path.join(outdir, CBF_filename))


        

    


