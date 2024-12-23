# Seer's Resource Worlds

A Fabric mod that makes it possible to add resource worlds. Resource worlds are intended to be torn
apart by your players, and periodically reset as a way to preserve the main overworld for people to
actually build within and enjoy for a long time.

This mod will create a separate overworld, nether, and end dimension. These dimensions are made 
available as persistent worlds that will be kept when your server restarts.

You can teleport to and from these worlds using the `/resource` command, like this:

```
/resource overworld
/resource nether
/resource end
/resource leave
```

When you teleport into a resource world, your position will be logged so that when you leave you can
return to where you were.

This mod also has specific built-in support for HuskHomes, so that if you teleport out via a home
your position will still be logged.

Portals will be disabled in resource worlds, meaning the only way in and out is via some form of
teleportation. All players will always have access to the built-in commands to enter and leave.
