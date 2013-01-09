# Execute from root directory

output="Skava.jar"
src="src"
bin="bin"
tmp="temporary_compiler_directory"

mkdir $tmp

cp -r $bin/* $tmp

cd $tmp

echo "Main-Class: Startup/Starter" > manifest
jar cmf manifest $output .

mv $output ..
cd ..

rm -r $tmp
