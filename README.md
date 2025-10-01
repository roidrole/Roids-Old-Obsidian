The original Jsoned Old Obsidian, when used with a lot of recipes, has output such as [this](https://spark.lucko.me/ekhDwoXL1Z) when a lot of recipes are registered

That report is the reason I made this mod. I do not have benchmarks, but the same user which created the report above (where 80% of the chunk loading time is used by Jsoned Old Obsidian) noticed no performance cost to adding recipes with this fork.

## Features

The basic feature or this mod is that when a block (let's call it liquid1) triggers a block update to another block (dust) that is next to a third block (liquid2), the dust gets converted to a blockstate (result) defined in JSON. Not that liquid1 and liquid2 don't have to be fluids.

For example, you could have fluid1 be lava, dust be redstone dust and fluid2 be water, and you'd get the old redstone -> lava conversion bug

This conversion, using the json API, would look like this :

```
{
    "liquid1": "minecraft:flowing_lava",
    "liquid2": [
        "minecraft:flowing_water",
        "minecraft:water"
    ],
    "dust": "minecraft:redstone_wire",
    "result": "minecraft:obsidian"
}
```

This mod supports blockstate matching, block matching, oreDict matching (using the picked block), RegEx matching (using registryname:meta) and any matching. It is fairly easy to add new matchers via GroovyScript native method access.

More details can be found on the [wiki](https://github.com/roidrole/Roids-Old-Obsidian/wiki)

### Reloading

You can also reload all conversions added by JSON by running the command /jsonedoldobsidian:conversion\_reload

## Performance

The mod this forks (Jsoned Old Obsidian), did RegEx matching for every entry on every NeighborNotifyEvent, thus wasting a lot of performance doing repeated expensive operations. This fork :

*   Avoid using RegEx where not necessary (i.e : block matching is made by ==)
*   Computes the Blocks, BlockStates, OreIds, RegEx (compilation) when the recipe is _added_ instead of matched (so it's only done once)
*   Hashes liquid1 matching

You can download this mod on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/roids-old-obsidian)