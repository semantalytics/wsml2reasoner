# Setup
DIR=/var/www/html/tools_deri_org/wsml2reasoner/nightly-build/wsml2reasoner_trunk
SNAPSHOT=../../snapshot

# The default on the build machine is sun java 1.5
# Uncomment this line to choose sun java 1.6
#export JAVA_HOME=/usr/lib/jvm/java-6-sun/

# This installation in barry's home directory has the ant junit plug-ins 
export ANT_HOME=/home/barryb/apache-ant-1.7.0

echo ====================================================================
echo WSML2Reasoner daily build script
date
echo

echo --------------------------------------------------------------------
echo Change to $DIR directory
cd $DIR

echo --------------------------------------------------------------------
echo Update from source control
svn up

echo --------------------------------------------------------------------
echo Build using ant
ant clean build-all-releases run-all-tests

echo --------------------------------------------------------------------
echo Make all new files group editable
chmod -R g+w *

echo --------------------------------------------------------------------
echo Remove yesterdays snapshot
rm -rf $SNAPSHOT/*

echo --------------------------------------------------------------------
echo Copy the WSML2Reasoner files and licenses
cp license-gpl.txt $SNAPSHOT
cp license-lgpl.txt $SNAPSHOT
cp build/release/* $SNAPSHOT

echo --------------------------------------------------------------------
echo Copy the javadoc to the snapshot
cp -r build/javadoc $SNAPSHOT

echo --------------------------------------------------------------------
echo Copy the test reports to the snapshot
cp -r build/report $SNAPSHOT

echo --------------------------------------------------------------------
echo And try to make absolutely sure that everything is group writable
chmod -R g+w $SNAPSHOT

