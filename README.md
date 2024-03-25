# Elevator simulator

Simulator for an elevator system that allows users to call an elevator and select
the destination floor on the panel before entering the elevator.

## Logic

### General rules of elevator behaviour

- Elevator fulfills all requests that are in a queue in ONE direction.
- The elevator cannot move when doors are open.
- If the elevator already closed the doors but new request is created on the same floor it will not change floor but
  will open the doors.
- All requests that are set for different direction are placed in the next queue to be fulfilled.
- Number of elevators is fixed and cannot be altered.

##### _Example_:

1. _Elevator is going up and is on floor 3, the next stop is 5._
2. _Someone calls an elevator on 4th floor and wants to go down._
3. _Elevator first fulfills the request and goes to floor 5, if all GoingUp requests for this part are fulfilled it
   changes direction and fulfills the request from 4th floor._

### Requests sequences

Each elevator has a requests collection that contains 3 segments:

1. First sequence - all floors that are a stop requests in one direction in order.
2. Second sequence - all floors that were requested but in the other direction in reversed order to first sequence.
3. Third sequence - floors that were requested when the first sequence already passed the pickup floors for them.

### Requesting an elevator

After providing a current floor and a target floor ElevatorService sends a request to all elevators to check
which one has the least steps to pick up the caller of the request.
Steps are calculated based on the potential pickup place in the request queue

#### Placing the request in the queue

When ElevatorService selects the elevator to fulfil the request the pickup floor is scheduled in the requests sequences.
The last stop for each sequence is called a target floor, and it's being updated when a new request is assigned to the
elevator.

#### Changing movement direction

When elevator reaches the target floor and the current request sequence is empty the two remaining request sequences are
assigned to the first and the second sequence. Then the elevator moves to the first stop regardless of the current
sequence direction.

#### End of requests

If the elevator fulfills all the requests it's set to Idle stage with doors closed - awaiting new request.

#### Example of full request execution for idle elevator:

1. Get a request and add it to the queue.
2. Move the elevator to the pickup floor.
3. Open door.
4. Close door.
5. Move to the target floor.

## Usage

1. Create the environment:
    1. Enter the number of available elevators for the simulation
    2. Provide lowest and the top floor
2. Start the simulation:
    1. See current status of all elevators using command `print` or `p`
    2. Perform one step of the simulation using command `step` or `s`
    3. Perform n-steps of the simulation using command `nsteps` or `n`
    4. Perform all queued steps of simulation using command `continue`
    5. See all possible commands using `menu` or `m`
    6. Add one or more elevator requests:
        1. One request: `calling_floor target_floor` (i.e.: `1 10` - call an elevator to the 1st floor, move up to the
           10th floor)
        2. Multiple requests separated
           by `,`: `call_floor1 target_floor1, call_floor2 target_floor2, call_floor3 target_floor3`

#### Potential improvements

- Better pickup score calculation that involves more factors: like steps to the target of the request; calculate the
  distance from current position.
- Add tests.
- Friendlier and more versatile user interface.
- Change the request sequence to some other collection that's ordered by nature.
- Add scalaDoc to all methods.
- Printing only changes in the queue instead of all information.
- Add support for malfunctioning elevators.
- Add support for total number of users in the elevator.

#### Difference from the initial task
Suggested solution was FCFS(first-come, first-serve) - this is good if we would like to have only one person in the elevator.
The approach presented here is allowing multiple requests the be placed in one elevator and fulfilling them along the way.
For example if elevator is on the 2nd floor moving up from floor 1 to 10, and someone requests an elevator on the 4th floor to 6th. It will pickup the caller and fulfill this request along the way.
Current solution also supports doors and the direction of movement.