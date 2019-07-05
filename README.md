# Looking to play bot

An over-engineered way to let people now that you're looking to play a certain game... #BlameJack

Users can join and leave groups that are tied to a specific game. You should then be able to simply `@mention` the group when you're looking to play that game.

## Structure
* Groups for games, single Discord Group per game. Needs allow mention permission. Same hierarchy as punished group.
* Group format: `game:identifier`
* Command structure: `@bot <command>`
* (Decouple the slap specific stuff from the music bot and move to this bot? So musicbot is just that, a music bot? Makes maintaining both bots easier.)
* Global config file under `yaml/config.yaml`

### Commands: Power users
* `@bot add/remove <identifier>` - Adds/Remove a game group. This needs to happen first, cause we need to create the group on discord. e.g.: when supplied with `lol`, discord group would be 'game:lol'. Typing `@lol` should still work with autocomplete.
* `@bot fullname <indentifier> <name>` - Sets the game's full name for rich text embeds.
* `@bot imagelink <indentifier>  <link>` - Sets an image for rich text embeds.

### Commands: Everyone 
* `@bot request <fullname>` - Request a new game be added to the bot. (To prevent users from creating thousands of groups, is this necessary for our small server?)
* `@bot info <identifier>` - rich embed with fullname, total number of players, list of online players
* `@bot subscribe/unsubscribe <identifier>` - Join/leave a game group. alias: `join`/`leave` `sub`/`unsub`
* `@bot teams <@exclusions>` - Move the teams command from the musicbot to this bot.

### Usage
Users can invite others by simply `@mentioning` the group... 
Alternatively, a looking-to-play-right-now which creates a rich text embed?

## Built using
See pom.xml for complete list
* JDA
* JDA Utilities
* Lombok

## Credits 
Part of the Slapgaming clan.

An idea by Denby.

Contributors:
- Rick Fontein (Telluur)

## Open source
Feel free to use this code under the provisions of the MIT license. 
