# Dragon Wings: Rebuild

An unofficial **NeoForge 26.3** port of [Dragon Wings](https://github.com/Xiaoyu-2009/Dragon-Wings) by [Xiaoyu_2009](https://space.bilibili.com/506597031).

Adds dragon wings to the player's back. Client-side only: no items, no blocks, no gameplay changes, and nothing is required on the server.

> **This is a third-party fork.** It is not affiliated with, sponsored by, or endorsed by the original author. All credit for the original mod, its models and its textures goes to Xiaoyu_2009. If you are looking for the 1.21.1 original, use [the upstream repository](https://github.com/Xiaoyu-2009/Dragon-Wings).

<!-- TODO: screenshot / GIF here -->

## Requirements

| | |
|---|---|
| Minecraft | 26.3 (not 26.2 — mods are not interchangeable between the two) |
| NeoForge | 26.3.0.101 or newer |
| Java | 25 |
| Side | Client only |

## Features

- Two wing types: **Ender Dragon Wings** and **Dragon Wings**
- Wings follow the player's body rotation, scale, and sneak offset
- Optional "expand while flying" behaviour
- Per-wing-type options, including whether other players' wings are rendered

## Installation

1. Install NeoForge for Minecraft 26.3.
2. Put `dragon_wings_rebuild-1.0.0.jar` into `.minecraft/mods`.
3. Launch the game. `config/dragon_wings_rebuild-client.toml` is generated on first run.

## Configuration

Everything is client-side, stored in `config/dragon_wings_rebuild-client.toml`, grouped per wing type.

| Key | Default | Description |
|---|---|---|
| `<type>_enabled` | Ender Dragon `true`, Dragon `false` | Render this wing type |
| `<type>_scale` | `100` | Wing scale, in percent |
| `<type>_flying_expand` | `false` | Expand the wings while flying |
| `<type>_show_other_players_wings` | `false` | Render this wing type on other players |

`<type>` is `ender_dragon_wings` or `dragon_wings`. Keys, defaults and ranges are identical to upstream, so an existing `dragon_wings-client.toml` can be copied over and renamed.

## Building

Requires JDK 25. If several JDKs are installed, point `JAVA_HOME` at the 25 one first.

```bash
./gradlew build
```

The jar is written to `build/libs/`.

`build.gradle` sets `disableRecompilation = true`: NeoForm's decompile/recompile step currently
fails on the 26.3.0.101 sources (`HolderSet$1.contents() ... HolderSet.Named.contents()`), and the
port does not need Minecraft sources to compile. Remove the flag if you want decompiled sources in
your IDE and your toolchain copes with that error.

## Status

Built and smoke-tested in a development client against NeoForge 26.3.0.101: the mod loads, the
config file is generated with the same keys and defaults as upstream, and both wing types render in
third person without errors. Not yet tested on a dedicated server (the mod is client-only and
declares `side = "CLIENT"` dependencies).

## Differences from upstream

- Ported from Minecraft 1.21.1 / NeoForge 21.1 to Minecraft 26.3 / NeoForge 26.3
- Java package changed from `net.xiaoyu.dragon_wings` to `dev.andows96.dragonwings` (third-party fork, not the author's namespace)
- Mod id changed from `dragon_wings` to `dragon_wings_rebuild`, so this jar can coexist with the original mod
- Config file renamed to `dragon_wings_rebuild-client.toml`
- Rendering adapted to the 26.x submit-node pipeline: `SubmitNodeCollector.submitModelPart` replaces the old `MultiBufferSource` + `ModelPart.render` path, and texture ids use `Identifier` instead of `ResourceLocation`
- Body rotation now comes from `AvatarRenderState.bodyRot` (interpolated) instead of the raw `Player.yBodyRot`; the difference is sub-pixel
- No gameplay or visual changes intended; behaviour is otherwise kept 1:1 with upstream

## Known limitations (inherited from upstream)

These are documented rather than fixed, because this port aims at behavioural parity with the 1.21.1 original:

- Visibility is decided entirely by the local client config, so two players with different configs see different things.
- `flying_expand` is only reliable for the local player; other players' ability data is not synced to the client.

## License

GPL-3.0, same as upstream. See [LICENSE](LICENSE).

---

## 中文说明

本仓库是 [Dragon Wings](https://github.com/Xiaoyu-2009/Dragon-Wings) 的**第三方非官方** 26.3 移植版，原作者为 [Xiaoyu_2009](https://space.bilibili.com/506597031)。模型与贴图版权归原作者所有，本仓库仅做版本适配。

- 纯客户端模组，服务器无需安装
- 两种翅膀：末影龙翅膀、龙翅膀
- 配置位于 `config/dragon_wings_rebuild-client.toml`，键名与默认值与原版完全一致
- 行为与 1.21.1 原版保持一致，已知问题见上方 *Known limitations*
- 只在 26.3 上测试过；26.2 与 26.3 的 mod 不通用
- 许可证沿用上游 GPL-3.0
