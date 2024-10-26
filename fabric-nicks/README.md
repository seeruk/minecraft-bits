# Seer's Nicks

A Fabric mod that allows cross-server nicknames. You don't need to be using a proxy, maybe you just 
have several servers and your players want their nickname to apply on all of them automatically.

**Features:**

* Nickname available as a placeholder in [Text Placeholder API](https://modrinth.com/mod/placeholder-api)
* Nickname available as a player display name override
* Name colour setting

**Solution:**

* Nicknames and colours are stored in MySQL
* Nickname updates are propagated via Redis pub/sub, so that each server can re-fetch an in-memory
cache of all nicknames
* Commands that interact with the database have a very short cooldown, per server
