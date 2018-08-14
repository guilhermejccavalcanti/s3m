
multipleLanguages() {
   
    cp -r otherlanguages $HOME/
    cd $HOME
    rm -rf .jfstmerge
    mkdir .jfstmerge
    cp otherlanguages/jfstmerge.statistics ./.jfstmerge/
    rm -rf repo
    mkdir repo
    cd repo
    git init
    cp ../otherlanguages/base$2 .
    git add .
    git commit -m "base"
    git checkout -b left
    rm base$2
    cp ../otherlanguages/left$2 base$2
    git add .
    git commit -m "left"
    git checkout master
    git checkout -b right
    rm base$2
    cp ../otherlanguages/right$2 base$2
    git add .
    git commit -m "right"
    git checkout master
    git merge left
    HAS_CONFLICT=$(git merge right | grep -c "CONFLICT")
    cd ../.jfstmerge
    CRYPTO_WORKED=$(ls | grep -c "defect")
    assertTrue "[ $HAS_CONFLICT -eq 1 ]"
    assertTrue "[ $CRYPTO_WORKED -eq 0 ]"
    cd ..
    rm -rf repo
    rm -rf .jfstmerge
}
