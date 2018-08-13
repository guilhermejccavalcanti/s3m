
multipleLanguages() {
   
    cp -r $1 $HOME/
    cd $HOME
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp $1/base$2 .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base$2
    cp $1/left$2 base$2
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base$2
    cp $1/right$2 base$2
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    assertTrue "[ $HAS_CONFLICT -eq 0 ]"
    cd ..
}
