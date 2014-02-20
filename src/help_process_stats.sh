
## Use this one to process the terminal output in case it is not already.
for f in stats_KTZ_*
do
  echo $f | sed -e "s/stats_\(.*\)/mkdir out_\1 ; rm out_\1\/\* ; cd $f ; \.\.\/process_term.sh ; cd - ; python analyze_stats.py -o out_\1 -i stats_\1 -t rbac_inter,rbac_intra ; cd out_\1 ; gnuplot \*\.p ; cd -/"
done

for f in stats_KTZ_*_1000checks*
do
  echo $f | sed -e "s/stats_\(.*\)/mkdir out_\1 ; rm out_\1\/\* ; cd $f ; \.\.\/process_term.sh ; cd - ; python analyze_stats.py -o out_\1 -i stats_\1 -t rbac_inter,rbac_intra ; cd out_\1 ; gnuplot \*\.p ; cd -/"
done



## Use this one to leave terminal output alone.
for f in stats_HWDS_KTZsubset*
do
  echo $f | sed -e "s/stats_\(.*\)/mkdir out_\1 ; rm out_\1\/\* ; python analyze_stats.py -o out_\1 -i stats_\1 -t rbac_inter,rbac_intra ; cd out_\1 ; gnuplot \*\.p ; cd -/"
done

for f in stats_HWDS_KTZ_*
do
  echo $f | sed -e "s/stats_\(.*\)/mkdir out_\1 ; rm out_\1\/\* ; python analyze_stats.py -o out_\1 -i stats_\1 -t rbac_inter,rbac_intra ; cd out_\1 ; gnuplot \*\.p ; cd -/"
done

for f in stats_HWDS_KTZ_0.0_1000checks
do
  echo $f | sed -e "s/stats_\(.*\)/python analyze_stats.py -o out_\1 -i stats_\1 -t rbac_intra ; cd out_\1 ; gnuplot \*\.p ; cd -/"
done


