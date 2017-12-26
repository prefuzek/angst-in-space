# Angst in Space

A WIP space strategy board game in Clojure disguised as a Quil sketch.

## Usage

The easiest way to play is just to run the .jar file from the [latest Github release](https://github.com/Sheep-Dip/angst-in-space/releases).

With the source code you can also try these:

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'angst.core)`.

## Gameplay

There are three ways to play:
- Locally, taking turns at the computer
- By email, sending the saved gamestate at the end of each turn. If you do this, just replace your old `save.txt` with the new one, then click 'Load' to get the new gamestate. (`save.txt` is created and read/written in the same directory as the .jar file.)
- Online - see Section 5: Online Play

### 1. Setup:

When you start the game, you'll have the choice of starting a local (offline) game, hosting an online game, or connecting to an online game. Here we'll go over starting a local game. For more information about online play, see section 5.

There are five available empires - to start a game you must choose at least two. There are two additional options that you can toggle: Random Start and Objectives. 

When you start the game, it will generate a galaxy based on how many empires are playing the game. If Random Start is turned on, each empire will start with two random connected planets. If it is left off, the game will use a pre-made setup (almost certainly more balanced than a random start). One of each player's planets will start with a higher development level.

If Objectives are turned on, each player will be given a random Objective (see below). No matter what options you choose, the game will start with each player having two connected planets and eight resources.

### 2. Objective:

If you have Objectives turned on, each empire will be assigned a victory condition at random. These determine how a player gets victory points (VP). The possible goals are:

- Conquistadores: Gain 3 VP each time you conquer an enemy planet
- Warlords: Gain 1 VP for each enemy ship you destroy and each planet you conquer
- Imperialists: Gain 1 VP for each empire with fewer planets than you at the begnning of each turn.
- Immortals: Gain 1 VP at the beginning of each turn.
- Slavers: Whenever you conquer an enemy planet, you gain 2 VP and they lose 2 VP.

NOTE: These are a work in progress, and may be unbalanced depending on the number of players.

So far, the game doesn't have a built-in end, but if you want you can agree to play until a certain number of rounds or VP. If you do, give each player 2 VP for each planet they control when the game ends. As you may have guessed, the player with the most VP is the winner.

If Objectives was turned off, there is no victory condition. You can do whatever you want without feeling restricted by some arbitrary measure of success - how liberating!

### 3. Taking your turn:

There are four main phases in each turn: Specialization, Production, Command, and Construction. In each phase, you can click on your planets to do the action associated with that phase. Each planet can be used only once per turn however, so make sure you plan your turns out efficiently!

#### 3.1. Specialization:

Each of your planets has a unique Planet Ability, shown in the planet description box when that planet is moused over. To activate a Planet ability, click on the planet, then follow any further prompts.

NOTE: Planet Abilities may be bug-infested, and interactions between Abilities may not work as expected or intended. If you find a bug, please document it in Issues.

##### 3.1.1. Projects:

Some planets have Projects as their Planet Abilities. These abilities, rather than being an instant event, accumulate progress(§) over time, and either provide bonuses based on how much Progress they have, or have actions that require you to spend progress to use. 

To begin a project, click on it during the Specialization phase. It will gain 1§ immediately, and will gain 1 more § at the start of each of your turns. While the project is active, you may not use the planet for any of the other main phases. If the Project has an action that costs §, you may click it again during the Specialization phase to use that action.

If you want to use a planet with an active Project in other phases again, you may stop the Project by right-clicking on it during the Specialization phase. It will lose all its §, but on your next turn you can use it in any phase. If a planet with an active Project is conquered, it will also be set to inactive, and lose all its §.

EXAMPLE 1: Byrd's Planet Ability is "Project: Reduce all movement costs by 1 for each §". During the Specialization phase, Matt decides to start Byrd's project. It gets one § immediately, so all movement costs are reduced by 1 this turn. He can no longer use Byrd to gain resources, command, or build ships while the project is active. At the start of his next turn, Byrd gains a second §, so all movement costs are reduced by 2 for the turn. On the turn after, Matt decides he needs Byrd for resources, so he stops the project by right-clicking on Byrd during the Specialization phase. Movement costs are no longer reduced, but he still cannot use the project until his next turn.

EXAMPLE 2: Xosa's Planet Ability is "Project: Spend 3§ to remove 2 ships from any planet." Sean starts Xosa's project, so it gains 1 progress. Two turns later, it has 3§, so he decides to use it (by clicking on it during his Specialization phase) and destroys two ships on an enemy planet. Sean then decides he doesn't want to wait another 3 turns to use it again, so he stops the project by right-clicking on Xosa. He will be able to use it however he wants on his next turn.

#### 3.2. Production:

In the Production phase, you can use your planets to generate resources. Resources are necessary to do pretty much anything, so this phase is quite important. Each planet has a production curve, which is a series of eight numbers that you can see by mousing over a planet. A planet's development level, marked by a circle around one of the production numbers, determines how many resources a planet will produce if you use it in the Production phase. At the end of each turn, all your planets' development levels will go up by one. Usually, this means they produce more resources, but some don't change, and some produce fewer resources as they develop.

Uncolonized planets don't have a development level. When you colonize a planet, it'll start the next turn at the lowest development level, and keep increasing from there.

Every time a planet is conquered, its development level is reduced by three - the infrastructure is destroyed and the workers revolt! 

#### 3.3. Command:

In the Command phase, you get to move your ships around. This lets you colonize new planets, defend yourself, and (most importantly) conquer other players' planets. When you choose a planet to command with, you can then move any ships from any planets connected to it. You can move these ships to any planet connected to the planet they're on. For example, say you chose to command with Fignon. Algoa is connected to Fignon, so you can move ships from Algoa to any planet that's connected to Algoa. Since Jaid is also connected to Fignon, you can, with the same action, move ships from Jaid to any planet connected to Jaid. In fact, with a single command action, you can command as many ships as you want (provided they're connected to the planet you chose to command with) to as many planets as you want (provided they're connected to the planet you're moving the ships from).

With a single command action, you'll only be able to move ships one space. If you command with a second planet, however, you can move ships that you've already moved this turn. In fact, if you really need to send a fleet across the galaxy, you can use as many planets to command as you want, and move the fleet one space for each planet! This can get pricey, but it's sometimes a good idea when you want to take someone by surprise.

Space travel is expensive. When you move a group of ships between planets, you'll have to pay resources equal to the distance between them (distance is marked by dots along the connection line). Moving five ships is just as expensive as moving one, so plan your moves carefully!

##### 3.3.1. Combat:

If another player happens to be occupying a planet that you send your ships to, you will engage in combat! There is absolutely no possibility of peaceful cohabitation (just like real life).

Also like real life is the fact that combat is 100% deterministic. When you attack another player, you will lose one of your ships for each ship that they have on that planet, plus another one if they control the planet that you're fighting over. Similarly, they lose one ship for each of your attacking ships. If you are attacking a planet that they control, and you have at least one ship surviving combat, congratulations! You are now in control of the planet. Unfortunately, it can't do anything until your next turn. If you destroy all of their ships, but none of yours survive, the planet stays in their posession.

If, for some reason, you're fighting over an uncolonized planet, whoever survives the combat will have to colonize it the hard way.

#### 3.4. Construction:

The construction phase is where you get to build your impressive armada. Click on planets to use them to build a ship there, at the low cost of two resources. Due to limited construction capabilities (these ships are big), you can only build one ship per planet per turn.

#### 3.5. Colonization:

At the end of your turn, you can colonize any uncolonized planet you have a ship on. Colonizing a planet it as easy as clicking on it, but it costs three resources, and one of your ships there will disappear (they retire from the Space Navy and settle down to tame the wilderness). On your next turn, it'll be ready to do whatever you want it to do.

#### 3.6. Upkeep:

Managing a large empire is hard and expensive work. At the end of your turn you will lose a certain amount of resources, calculated based on how many planets you control. You can see what your current upkeep rate is in the Empire display in the lower right of the screen, so you should never be taken by surprise. If you don't have enough resources, you'll dip into the negative, and you'll have to claw your way back up before you can build and move ships, or colonize planets!

### 4. Ending the game:

That's all the rules! Eventually there will be more, as more features are implemented, but for now you can go out and build angsty empires to your heart's content. The game won't force an end on you - isn't that nice?

### 5. Online Play:

As of version 0.6.0, you can now play online in real time! This feature is still in development and is therefore not totally streamlined or stable yet, but it's completely functional.

From the main menu, you can host an online game. After choosing a name, this sets up a local server, which can be accessed by anyone on your local network. If you want to play remotely, you'll need a tool like Hamachi to simulate a local network.

If someone on your network is hosting a game, you can connect to it from the main menu. After entering a name and the host's IP, you'll be sent into a lobby with a list of connected players and a chatbar. The host has setup options for a new game, or can load from a previous save.

Once the game has started, you won't be able to interact with the game (besides menus and chat) except on your turn. If you leave and reconnect, you'll be put in the game's lobby with now way of playing the game. This will be fixed in a future version.

## License

Distributed under the Eclipse Public License, the same as Clojure.
