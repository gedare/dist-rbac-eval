#!/bin/bash

for d in `ls`
do
  if [ -d $d ]; then
    cd $d
    for f in `ls _term*.txt`
    do
      filename=`echo $f | sed -e "s/_term/_stats/"`
      echo -n "" > ${filename}
      cat $f |
        while read times init access destroy x iter; \
        do \
          if [ ${times} == "Times" ]; then \
            echo "${init} ${access} ${destroy}" >> ${filename}; \
          elif [ ${times} == "cov:" ]; then \
            echo "${iter}" >> ${filename}; \
          fi; \
        done;
    done
    cd -
  fi
done


