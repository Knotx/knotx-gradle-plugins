#!/usr/bin/env bash

repos=()
IFS=$'\n' read -d '' -r -a repos < ../repositories.cfg

for project in "${repos[@]}"
do
  projectDir=~/Workspace/KNOTX/${project}/.git
  operation="Processing $project in $projectDir"
  echo "$operation"

  git --git-dir=$projectDir add .
  git --git-dir=$projectDir ci -m "Knotx/knotx#511 Common contributing rules"
  hub --git-dir=$projectDir pull-request --base master -p -F update-message.md

done

echo "***************************************"
echo "Finished!"
