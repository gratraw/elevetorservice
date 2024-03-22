## Elevator simulator
Simulator for an elevator system that allows users to call an elevator and select 
the destination floor on the screen before entering the elevator.

### Logic
#### Selecting an elevator
After providing a current floor and a target floor

### Usage
1. Create the environment: 
   1. Enter the number of available elevators for the simulation
   2. Provide lowest and the top floor
2. Start the simulation:
   1. See current status of all elevators using command `status` or `s`
   2. Perform one step of the simulation using command `step`
   3. Perform all queued steps of simulation using command `continue`
   4. See all possible commands using `menu` or `m`
   5. Add one or more elevator requests:
      1. One request: `calling_floor target_floor` (i.e.: `1 10` - call an elevator to the 1st floor, move up to the 10th floor)
      2. Multiple requests separated by `,`: `call_floor1 target_floor1, call_floor2 target_floor2, call_floor3 target_floor3`

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
