#!/bin/bash

parentFolder=$(echo "${1%/*}")

sed 'a\\$\$\$\$\$\$\$' $1 > "$1_temp"
sed 'a\\$\$\$\$\$\$\$' $2 > "$2_temp"
sed 'a\\$\$\$\$\$\$\$' $3 > "$3_temp"

diff3 -m "$1_temp" "$2_temp" "$3_temp" > "$parentFolder/mid_merged"

rm "$1_temp"
rm "$2_temp"
rm "$3_temp"

sed ':a;N;$!ba;s/\n\$\$\$\$\$\$\$//g' "$parentFolder/mid_merged" > "$parentFolder/merged"

rm "$parentFolder/mid_merged"

ESCAPED_LEFT=$(printf '%s\n' "$1" | sed -e 's/[\/&]/\\&/g')
ESCAPED_BASE=$(printf '%s\n' "$2" | sed -e 's/[\/&]/\\&/g')
ESCAPED_RIGHT=$(printf '%s\n' "$3" | sed -e 's/[\/&]/\\&/g')

ESCAPED_TEMP_LEFT=$(printf '%s\n' "$1_temp" | sed -e 's/[\/&]/\\&/g')
ESCAPED_TEMP_BASE=$(printf '%s\n' "$2_temp" | sed -e 's/[\/&]/\\&/g')
ESCAPED_TEMP_RIGHT=$(printf '%s\n' "$3_temp" | sed -e 's/[\/&]/\\&/g')

sed "s/\(<<<<<<< $ESCAPED_TEMP_LEFT\)\(.\+\)/\1\n\2/" "$parentFolder/merged" \
| sed "s/\(<<<<<<< $ESCAPED_TEMP_BASE\)\(.\+\)/\1\n\2/" \
| sed "s/\(<<<<<<< $ESCAPED_TEMP_RIGHT\)\(.\+\)/\1\n\2/" \
| sed "s/\(||||||| $ESCAPED_TEMP_BASE\)\(.\+\)/\1\n\2/" \
| sed "s/\(||||||| $ESCAPED_TEMP_LEFT\)\(.\+\)/\1\n\2/" \
| sed "s/\(||||||| $ESCAPED_TEMP_RIGHT\)\(.\+\)/\1\n\2/" \
| sed "s/\(>>>>>>> $ESCAPED_TEMP_RIGHT\)\(.\+\)/\1\n\2/" \
| sed "s/\(>>>>>>> $ESCAPED_TEMP_LEFT\)\(.\+\)/\1\n\2/" \
| sed "s/\(>>>>>>> $ESCAPED_TEMP_BASE\)\(.\+\)/\1\n\2/" \
| sed "s/\(=======\)\(.\+\)/\1\n\2/" \
| sed "s/$ESCAPED_TEMP_LEFT/$ESCAPED_LEFT/g" \
| sed "s/$ESCAPED_TEMP_BASE/$ESCAPED_BASE/g" \
| sed "s/$ESCAPED_TEMP_RIGHT/$ESCAPED_RIGHT/g" > $4

rm "$parentFolder/merged"