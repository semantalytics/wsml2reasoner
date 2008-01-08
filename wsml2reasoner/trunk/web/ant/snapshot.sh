# Setup
#DIR=/var/www/html/iris-reasoner_org/ant/iris
DIR=/var/www/html/tools_deri_org/wsml2reasoner/nightly-build/wsml2reasoner
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
#cvs update -d
svn up

echo --------------------------------------------------------------------
echo Clean distribution dir
rm -rf dist/**.*

echo --------------------------------------------------------------------
echo Build using ant
ant release

echo --------------------------------------------------------------------
echo Make all new files group editable
chmod -R g+w *

echo --------------------------------------------------------------------
echo Remove yesterdays snapshot
rm -rf ../../snapshot/*.zip
rm -rf ../../snapshot/*.jar

echo --------------------------------------------------------------------
echo Copy the WSML2Reasoner files and licenses
cp license-gpl.txt ../../snapshot/
cp license-lgpl.txt ../../snapshot/
cp dist/* ../../snapshot/

echo --------------------------------------------------------------------
echo Copy the javadoc to the snapshot
cp -r javadoc/* ../../snapshot/javadoc/

echo --------------------------------------------------------------------
echo And try to make absolutely sure that everything is group writable
chmod -R g+w ../../snapshot

