$dir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $dir\..\..

Write-Output "" > out.txt

java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 1 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 1 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 2 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 2 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 3 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 3 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 4 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 4 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 5 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 5 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 6 0 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 6 0 >> out.txt