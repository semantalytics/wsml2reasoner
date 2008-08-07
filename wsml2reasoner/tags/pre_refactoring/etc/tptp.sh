#!/bin/bash
PATH=/home/spass+T/SPASS+T/:$PATH
tptp2X=/home/tptp/TPTP-v3.2.0/TPTP2X/tptp2X
dir=/tmp
filename="somefile";
cd $dir
$tptp2X -fdfg $filename.tptp;
echo `/home/spass/SPASS -TimeLimit=30 dfg/$filename.dfg | grep "SPASS beiseite"&`;
