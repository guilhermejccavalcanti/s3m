rm -rf repo
mkdir repo
cd repo
git init
cp ../exemplo/base.java .
git add .
git commit -m "base"
git checkout -b left
rm base.java
cp ../exemplo/left.java base.java
git add .
git commit -m "left"
git checkout master
git checkout -b right
rm base.java
cp ../exemplo/right.java base.java
git add .
git commit -m "right"
git checkout master
git merge left
DIFF_WORKED=$(git diff left right | grep -c "@@ -1,5 +1,9 @@")
if [ $DIFF_WORKED -eq 1 ]; then
    cd ..
    rm -rf repo
    exit 0
else
    cd ..
    rm -rf repo
    exit 1
fi
