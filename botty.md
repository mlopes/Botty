Bots Game

Bots game(replace with a proper name) is a realtime game. Players make http requests to play, get some information back, and decide on the next move. The game is updated every time a client makes a request. Movement intervals are capped, to prevent faster computers/connection to have advantage over the others.

[[TOC]]

# Communicating with the game

## Managing bots

### Player registers bot and gets a key

**POST /bot/register**

Request:

{**'name'**: 'My Bot'}

Response:

{ **'key'**: 'A4F912B'}

From now on, all requests take a header with the bot key.

## Starting a game ceremonies

### Player starts a new game

**POST /game/new**

Response:

{ **'gameKey'**: 'AF563BA99C241'}

A new game is started and the player starting the game is joined.

### Player joins a game

**POST /game/join/:gameKey**

The player is joined to an existing game

### Players acknowledge start timestamp

**POST /game/start**

Response:

{

   **'startGameTimestamp'**: 1518620062,

   **'players'**: [

   'Bob',

   'HAL',

   'Marvin',

   'Data'

  ]

}

The request will be pending until all the players acknowledge or will timeout. When all players acknowledged start, the timestamp of the game start is sent back. This serves the purpose of confirming that all players can start at the same time, and all know when they can start making requests to the game.

## Playing a game

Move can be 0 (don’t move), or 1 (move forward)

Rotate can be cw (rotate clockwise), ccw (rotate counter clockwise) or not sent, in which case the player doesn’t rotate.

Shield can be 0 to disable the shield, 1 to enable it, or not sent to keep it unaffected. If shield is on, the player won’t be able to move or shoot, but will still be able to rotate.

Shoot, if 1 the player will fire a shot, if not sent it does nothing. The player can’t move or rotate while shooting.

All positions are relative to the player’s spawn point.

**POST /play/move**

Request:

{

 **'move'**: 0,

 **'rotate'**: 'cw',

 **'shield'**: 1,

 **'shoot'**: 1

}

Response:

{

 **'view'**: ['', '', '@', '%', '', '#', ''],

 **'hp'**: 100,

 **'position'**: {**'x'**: 2, **'y'**: 10},

 **'controlledCheckpoints'**: [

   { **'x'**: 8, **'y'**:  3},

   { **'x'**: 6, **'y'**: 6}

 ],

 **'gameStatus'**: {

   **'status'**: 'running',

   **'score'**: {

     **'Bob'**: 12,

     **'HAL'**: 18,

     **'Marvin'**: 0,

     **'Data'**: 100

   }

 }

}

View shows the contents of the tiles to which the player is facing

HP is the player’s current health points

Position is the player’s current position

ControlledCheckpoints is the list of positions of the checkpoints the player currently controls

Points is the player’s current score

gameStatus contains information about the game. Status can be running or finished. Score contains… the scores.

# Game rules

## Seeing the board

The bots can see a certain distance towards where they’re facing (maybe 5 squares vertically/horizontally and 4 diagonal?). If there’s a wall or trees in the way, the player won’t be able to see through them.

Paired with all positional coordinates being relative to the player’s spawn point, this allows for writing AI to explore the map and build knowledge of the map throughout the game.

## Map topography

There’s hazards (to be defined) ex: water the player falls and dies; walls the player tries to move into them and loses HP.

The terrain is plane, so every square on the grid is either occupied by a solid object, an hazard or is a walkable square.

## Movement

The players can move at will, but movement speed is capped on the server. This is to prevent players from faster computers/connections to move faster than other players.

The player can only request to move one step forward on each call.

If the player rotates and moves at the same time, we can do one of three things, yet to be decide:

1. Move first, then rotate - This is the easiest one. It means the player is moving towards a known square that is right in front of their field of view. It is also the least useful interaction and the most boring one.

2. Rotate first, then move - This means that:

    1.  either the player has a built knowledge of the map, which tells them it’s safe to jump to a square they’re not currently facing, or better yet, they can know from their calculations that there’s another player there and perform a surprise attack

    2. The player is taking a risk and knows nothing about the square they’re about to jump to

3. Give the player the option to specify which kind of movement they want to do in the call.

Rotation speed is also capped on the server, the player can rotate across 8 directions N NE E SE S SW W NW. The player will start facing a random direction picked amongst N S E W; I.E the player will never start facing a diagonal. Note that the direction identifiers are for this documentation purposes only, this information is not passed to the bots as it is irrelevant, since the bots build their own map.

## Combat

### Shooting

Players can shoot within a limited range.

Shooting frequency is capped on the server, as well as weapon cool off time. This serve the purpose of preventing shot spam, as well as prevent advantage from faster computers/connections.

Shots have limited range, they won’t go across the whole board (maybe 4 squares vertically/horizontally, and 3 diagonal?). Shots will stop if they hit a solid square (more information about solid squares in the [Map topography](#heading=h.ptgys9qgym7r) section).

### Melée

For melée combat to happen, the players will have to be in adjacent squares. 

In melée combat, if a player is not facing the attacker, then they’ll take damage and will be bumped by the attacker. If they’re facing the attacker, then damage will be split arbitrarily amongst the two parties involved, and both will be bumped back.

## Powerups

There’s power-ups (to be defined) ex: replenish health; extra points

## Scoring and winning

The players get points by picking up powerups or controlling checkpoints. Checkpoints give points over time. When the player dies, they lose their points.

A player can steal another one’s checkpoints by coming in contact with them.

When a player dies, they will return to the game, but will lose their checkpoints and part (or maybe all?) of their points.

A player can die by stepping/hitting a hazard, as a result of meleé combat or being shot by another player.

After the time ends, the game finishes and the player with most points wins.

