$dir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $dir\..\..

Write-Output "" > out.txt

java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllCondition 01011 00000 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllCondition 01011 00000 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllCondition 01011 00000 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllConditionConcurrent 01011 00000 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllConditionConcurrent 01011 00000 >> out.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllConditionConcurrent 01011 00000 >> out.txt