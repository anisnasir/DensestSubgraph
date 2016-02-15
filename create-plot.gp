set terminal postscript eps enhanced color "Arial,18" linewidth 3
set output "plot-time.eps"

set size 1.8,0.6


set multiplot
#set style data linespoints
set style data linespoints
set pointintervalbox 1.5

set pointsize 1.5

set style line 1 lt rgb "#A00000" lw 2 pt 1
set style line 2 lt rgb "#00A000" lw 2 pt 6
set style line 3 lt rgb "#5060D0" lw 2 pt 2
set style line 4 lt rgb "#F25900" lw 2 pt 9
set style line 5 lt rgb "#00CED1" lw 2 pt 4

unset key
set key t l box
set title "insertion time"
set origin 0,0
set size 0.6,0.6
set xlabel "keys"
set ylabel "processing time"
#set logscale y
#set yrange [0:70]
set format x "10^{%L}"
#set format x "%t"
#set xtics (0.4, 0.8, 1.2, 1.6, 2.0)
#set logscale x; set xtics 1,100,1e8

plot "DensestSubgraph/output_insertion_com-lj.ungraph.txt" title "processing time"

unset key
set key t l box
set title "deletion time"
set origin 0.6,0
set size 0.6,0.6
set xlabel "keys"
set ylabel "processing time"
#set logscale y
#set yrange [0:70]
#set format y "10^{%L}"
#set format x "%t"
#set xtics (0.4, 0.8, 1.2, 1.6, 2.0)

plot "DensestSubgraph/output_deletion_com-lj.ungraph.txt" title "processing time"



!epstopdf "plot-time.eps"
