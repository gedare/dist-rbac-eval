#!/bin/bash

ls -1 | grep CBF | sed -e 's/\(_term.*\).CBF/mv \1.CBF \1.txt/'

for f in `ls _term*`
do
  dos2unix $f
  grep -e algorithm -e cov -e fatal $f | while read a b c d e f; \
  do \
  if [ $a != '#' ]; then \
    echo  -n "$b " ; \
    if [ ! -z $c ]; then \
      echo $d $f; \
    fi; \
  else echo "0 0 0" ; \
  fi; \
  done > processed$f
  cat processed$f | while read alg cov mean iter; \
  do \
    filename=`echo $f | sed -e "s/_term/_stats/" -e "s/.txt/.${alg}.CBF/"` ; \
    echo "${mean} ${cov}" > ${filename}; \
  done
done

