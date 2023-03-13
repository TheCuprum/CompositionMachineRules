$dir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $dir\..\..

Write-Output "" > out.txt

java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 1 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 1 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 2 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 2 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 3 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 3 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 4 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 4 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 5 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 5 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 6 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatchOptimized 6 6 0 >> out.txt