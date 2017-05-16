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
HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
if [ $HAS_CONFLICT -eq 1 ]; then
    cd ..
    rm -rf repo
    exit 0
else
    cd ..
    rm -rf repo
    exit 1
fi
