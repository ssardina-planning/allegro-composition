#!/bin/bash

#COUNT=1

#while [ $COUNT -gt 0 ]; do
	echo Clearing cache
	sudo echo 3 | sudo tee /proc/sys/vm/drop_caches
	#sleep 60
#done

