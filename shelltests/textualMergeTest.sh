rm -rf repo
mkdir repo
cd repo
git init
cp ../exemplotxt/base.java .
git add .
git commit -m "base"
git checkout -b left
rm base.java
cp ../exemplotxt/left.java base.java
git add .
git commit -m "left"
git checkout master
git checkout -b right
rm base.java
cp ../exemplotxt/right.java base.java
git add .
git commit -m "right"
git checkout master
git merge left
IS_RECURSIVE=$(git merge right -m "test merge" | grep -c "recursive")
if [ $IS_RECURSIVE -eq 1 ]; then
    cd ..
    rm -rf repo
    exit 0
else
    cd ..
    rm -rf repo
    exit 1
fi
