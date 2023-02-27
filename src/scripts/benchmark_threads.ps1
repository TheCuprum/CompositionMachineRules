$dir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $dir\..\..

Write-Output "" > out.txt

java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 1 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 1 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 2 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 2 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 3 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 3 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 4 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 4 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 5 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 5 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 6 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 4 6 >> out.txt