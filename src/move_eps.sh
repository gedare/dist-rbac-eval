#!/bin/bash

dst=/home/gedare/work/research/gab_papers/RBAC/Dist_HWDS/eps

for d in out_HWDS_KTZ_0.0_1000checks_60sessions
#for d in out_KTZ_0.0
#for d in out_KTZ*
do
  case $1 in
    0)
  ### norotate x-axis, legend at bottom-left
  for f in interatCore_line
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;

    1)
  # move legend to top
  for f in interdtCore_line interitCore_line
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;

  ### rotate x-axis, legend at top-left, offset by 0.5
    2)
  for f in intra_perms_at_line intra_roles_at_line
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;

    3)
  # move legend to top
  for f in intra_perms_at_box intra_perms_it_line
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;

    4)
  # offset top by 100
  for f in intra_perms_dt_line #intra_roles_dt_line 
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;

    5)
  # offset top by 500
  for f in intra_roles_it_line intra_perms_it_line
  do
    mkdir ${dst}/${f}
    cp ${d}/${f}.eps ${dst}/${f}/${d}_${f}.eps
  done
  ;;


  *) echo "specify option 0-3";;
esac
done

