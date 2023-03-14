$dir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $dir\..\..

Write-Output "" > benchmark_threads.txt

java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 1 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 1 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 2 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 2 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 3 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 3 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 4 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 4 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 5 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 5 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 6 0 >> benchmark_threads.txt
java -cp .\CompositionMachineRules.jar cuprum.cmrule.TestAllMatch 8 6 0 >> benchmark_threads.txt