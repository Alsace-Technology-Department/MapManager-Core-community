# MapManager

A plugin made by CHuNan, which could help you manage your worlds

## Commands

Here are all commands which MapManager registered.

* /world admins

  List all admins in a world.

* /world admin \<add|remove\> \<id\>

  Add an admin to or remove an admin from a world.

* /world builders

  List all builders in a world.

* /world builder \<add|remove\> \<id\>

  Add a builder to or remove a builder from a world.

* /world visitors

  List all visitors in a world.

* /world visitor \<add|remove\> \<id\>

  Add a visitor to or remove a visitor from a world.

  Tip: Fill the \<id\> with "*" in order to make your world public.

* /world physics \[info|true|false\]

  * info or null - Show whether the physics turned on in the world.
  * true - Turn on the physics
  * false - Turn off the physics
  
* /world explosion \[info|true|false\]

  * info or null - Show whether the world has explosion.
  * true - Turn on the explosion.
  * false - Turn off the explosion.

* /world pvp \[info|true|false\]

  * info or null - Show whether the world can pvp.
  * true - Turn on the pvp.
  * false - Turn off the pvp.

* /world kick <id>

  Kick a player out of your world.

* /world setname <name>

  Set the name of the world.

* /world setspawn

  Set the spawn location.

* /world reload

  Permission "mapmanager.administrator" is required.

  Reload the config.

* /import n:\<name\> a:\[alias\] c:\[color\] p:\<permission group\> o:\<owner\>

  Permission "mapmanager.command.import" is required.

  Import and initialize a world.

* /delete \[world\]

  Permission "mapmanager.command.delete" is required.

  Delete a world.

* /write

  Permission "worldmanager.command.write" is required.

  Print details of all worlds.
