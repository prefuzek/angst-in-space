# Angst in Space

A WIP space strategy board game in Clojure disguised as a Quil sketch.

## Usage

The easiest way to play is just to run the .jar file from the [latest Github release](https://github.com/Sheep-Dip/angst-in-space/releases).

With the source code you can also try these:

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'angst.core)`.

## Gameplay

You can either play locally, taking turns at the computer, or you can play by email, sending the saved gamestate at the end of each turn. If you do this, just replace your old `save.txt` with the new one, then click 'Load' to get the new gamestate. (`save.txt` is created and read/written in the same directory as the .jar file.)

### Objective:

Well, right now there isn't really one. I guess you can try to completely dominate the galaxy, but that would end up getting very long and tedious. Hopefully a victory condition will be added soon (if nothing else, a simple placeholder measure of success).

### Setup:

It's all done for you when you launch the game. Four players, four empires: the Sheep Empire (Blue), the Gopher Empire (Green), the Muskox Empire (Red), and the Llama Empire (Yellow). Each empire starts with two planets and 8 resources.

### Taking your turn:

There are four phases in each turn: Specialization, Production, Command, and Construction. In each phase, you can click on your planets to do the action associated with that phase. Each planet can be used only once per turn however, so make sure you plan your turns out efficiently!

#### Specialization:

The Specialization phase action hasn't been implemented yet. In the full game, this is the phase where you use your planets' fancy special abilities. Right now, clicking on planets during the Specialization phase does nothing except use that planet's action for the turn, so it's usually not a good idea.

#### Production:

In the Production phase, you can use your planets to generate resources. Resources are necessary to do pretty much anything, so this phase is quite important. Each planet has a production curve, which is a series of eight numbers that you can see by mousing over a planet. A planet's development level, marked by a circle around one of the production numbers, determines how many resources a planet will produce if you use it in the Production phase. At the end of each turn, all your planets' development levels will go up by one. Usually, this means they produce more resources, but some don't change, and some produce fewer resources as they develop.

Uncolonized planets don't have a development level. When you colonize a planet, it'll start the next turn at the lowest development level, and keep increasing from there.

Every time a planet is conquered, its development level is reduced by three - the infrastructure is destroyed and the workers revolt! 

#### Command:

In the Command phase, you get to move your ships around. This lets you colonize new planets, defend yourself, and (most importantly) conquer other players' planets. When you choose a planet to command with, you can then move any ships from any planets connected to it. You can move these ships to any planet connected to the planet they're on. For example, say you chose to command with Fignon. Algoa is connected to Fignon, so you can move ships from Algoa to any planet that's connected to Algoa. Since Jaid is also connected to Fignon, you can, with the same action, move ships from Jaid to any planet connected to Jaid. In fact, with a single command action, you can command as many ships as you want (provided they're connected to the planet you chose to command with) to as many planets as you want (provided they're connected to the planet you're moving the ships from).

With a single command action, you'll only be able to move ships one space. If you command with a second planet, however, you can move ships that you've already moved this turn. In fact, if you really need to send a fleet across the galaxy, you can use as many planets to command as you want, and move the fleet one space for each planet! This can get pricey, but it's sometimes a good idea when you want to take someone by surprise.

Space travel is expensive. When you move a group of ships between planets, you'll have to pay resources equal to the distance between them (distance is marked by dots along the connection line). Moving five ships is just as expensive as moving one, so plan your moves carefully!

##### Combat:

If another player happens to be occupying a planet that you send your ships to, you will engage in combat! There is absolutely no possibility of peaceful cohabitation (just like real life).

Also like real life is the fact that combat is 100% deterministic. When you attack another player, you will lose one of your ships for each ship that they have on that planet, plus another one if they control the planet that you're fighting over. Similarly, they lose one ship for each of your attacking ships. If you are attacking a planet that they control, and you have at least one ship surviving combat, congratulations! You are now in control of the planet. Unfortunately, it can't do anything until your next turn. If you destroy all of their ships, but none of yours survive, the planet stays in their posession.

If, for some reason, you're fighting over an uncolonized planet, whoever survives the combat will have to colonize it the hard way.

#### Construction:

The construction phase is where you get to build your impressive armada. Click on planets to use them to build a ship there, at the low cost of two resources. Due to limited construction capabilities (these ships are big), you can only build one ship per planet per turn.

#### Colonization:

If you have ships on uncolonized planets at the end of the turn, you get a bonus phase! Colonizing a planet it as easy as clicking on it, but it costs three resources, and one of your ships there will disappear (they retire from the Space Navy and settle down to tame the wilderness). On your next turn, it'll be ready to do whatever you want it to do.

#### Upkeep:

Managing large empire is hard and expensive work. At the end of your turn you will lose a certain amount of resources, calculated based on how many planets you control. You can see what your current upkeep rate is in the Empire display in the lower right of the screen, so you should never be taken by surprise. If you don't have enough resources, you'll dip into the negative, and you'll have to claw your way back up before you can build and move ships, or colonize planets!

### Ending the game:

That's all the rules! Eventually there will be more, as more features are implemented, but for now you can go out and build angsty empires to your heart's content. The game won't force an end on you - isn't that nice?

## FAQ:

- This isn't a full game! Is this all there is?
  - So far, yes, this is all there is. The board game version, however, is much more feature-complete, and I intend to work at putting all those features in.
- Why aren't you using \<insert awesome Clojure/Quil feature>?
  - I'm rather new to Clojure, so it's probably just because I don't know about it. If you know of something that would make my life easier, I'd love to hear about it!
- Why is the code so poorly documented?
  - When I began this project, I just wanted to make something that worked as quickly as possible. Now that I've got that, I plan on improving the code and documentation that I have, in addition to adding new features.
- Why angst?
  - The game's title is the name of a movie that's mentioned in passing in Douglas Adams' novel Life, the Universe, and Everything. It's not really intended to mean anything, but it occasionally describes the attitudes of a player rather accurately.

## License

Distributed under the Eclipse Public License, the same as Clojure.