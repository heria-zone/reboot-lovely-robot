# Changelog

All notable changes to the LovelyRobot project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Versioning Scheme

This project uses [Semantic Versioning](https://semver.org/) with the format `MAJOR.MINOR.PATCH`:

- **MAJOR**: Incompatible API changes, major feature overhauls, or breaking changes to robot behavior
- **MINOR**: New functionality added in a backwards-compatible manner, new robot types, or significant feature additions
- **PATCH**: Backwards-compatible bug fixes, minor improvements, or compatibility updates

### Version Components
- **Pre-release**: Alpha (`1.0.0-alpha.1`) and Beta (`1.0.0-beta.1`) versions for testing
- **Build metadata**: Minecraft version compatibility (`1.0.0+mc1.20.1`)

## [Unreleased]

### Planned for Next Release
- Development environment setup for all three project variants
- Enhanced documentation and contribution guidelines
- Automated build pipeline improvements

### In Development
- **LovelyRobot: Tribute** - Faithful recreation of the original mod
- **LovelyRobot: Legacy** - Enhanced version with expanded features
- **LovelyRobot: Reboot 2.0** - Advanced version with robot creator system

### Recent Development Progress (November 2025)

#### Added
- **Legacy Project Expansion**: Complete legacy project structure for multiple Minecraft versions
  - **Minecraft 1.7.10**: Ancient version support with specialized Forge implementation and JitPack configuration
  - **Minecraft 1.12.2**: Complex version with workaround implementation (future multi-loader refactoring planned)
  - **Minecraft 1.16.5**: Legacy project with refined build configurations
  - **Minecraft 1.17.1**: Legacy project with complete Fabric and Forge implementations
  - **Minecraft 1.18.2**: Legacy project with event handling foundation
  - **Minecraft 1.19.2**: Legacy project with Lovely naming convention
  - **Minecraft 1.19.4**: Legacy project with comprehensive Lovely-prefixed classes
  - **Minecraft 1.20.1**: Updated legacy project with standardized naming
- **Tribute Project Structure**: Complete tribute variant (tlovelyr-1.20.1) with both Fabric and Forge support
- **Reboot Project Foundation**: Reboot version (rlovelyr-1.20.1) with modern implementation approach
- **Shared Resources**: Common resource structure for all project variants
  - Shared animations, geometry definitions, and textures
  - Common data files including recipes and item tags
  - Centralized resource management for consistency
- **Documentation Infrastructure**: Comprehensive documentation system
  - Development task documentation including environment setup
  - Coding guidelines for Java development
  - Project changelog and README guidelines
  - November 2025 development checklist and TODO lists
  - Roadmap documentation for characteristics and refactory updates
- **Configuration Management**: 
  - Root .gitattributes configuration for consistent file handling
  - Project-wide build template system
  - Update tracking system with llovelyr.json configuration

#### Changed
- **Class Naming Convention**: Standardized to "Lovely" prefix across all legacy projects
  - Renamed Default/Example classes to LovelyBlocks, LovelyEvents, LovelyGroups, LovelyItems
  - Updated data generators and mixins to use Lovely prefix
  - Reorganized configuration classes into configs package
- **Project Structure**: Reorganized resource files from root resources/ to sources/common/resources/
- **Build System**: Updated build configurations for multiple Minecraft version compatibility
- **Asset Management**: Improved asset organization with banner.png and icon.png updates

#### Removed
- **Legacy Resource Structure**: Cleaned up old resources/ directory structure
- **Outdated Documentation**: Removed obsolete documentation files from docs/useful/
- **Individual .gitattributes**: Consolidated project-specific .gitattributes into root configuration
- **Temporary Files**: Cleaned up build template files and development artifacts
- **FUNDING.yml**: Removed funding configuration file

#### Fixed
- **Multi-Version Compatibility**: Resolved build configuration issues across different Minecraft versions
- **Resource Organization**: Established proper shared resource structure for all project variants
- **Documentation Consistency**: Aligned documentation with current project structure

#### Technical Infrastructure
- **Multi-Loader Architecture**: Foundation for proper multi-loader approach with native Java common code
- **Legacy Version Support**: Comprehensive support spanning Minecraft 1.7.10 through 1.20.1
- **Development Tooling**: Enhanced development environment with comprehensive documentation
- **Build Templates**: Standardized build configurations for consistent development experience

### Project Foundation

#### Added
- **Project Foundation**: Established recreations of the original LovelyRobot mod
- **Multi-Platform Support**: Available on both CurseForge and Modrinth platforms
- **Cross-Version Compatibility**: Support for Minecraft 1.7.10, 1.12.2, 1.16.5, 1.17.1, 1.18.2, 1.19.2, 1.19.4, 1.20.1, 1.21.x
- **Development Infrastructure**: 
  - Gradle build system with multi-platform support
  - IntelliJ IDEA project configurations
  - Forge and Fabric compatibility layers
- **Documentation**: Updated README with installation and usage instructions
- **Community Features**: Discord server integration and GitHub issue tracking

#### Changed
- **Architecture**: Rebuilt from ground up using modern Minecraft modding practices
- **Code Organization**: Implemented clean architecture with separation of concerns

### Technical Details
- **Dependencies**: GeckoLib required for animation rendering
- **Platforms**: CurseForge and Modrinth distribution
- **Licensing**: MIT License for open-source development
- **Development**: Java-based with Minecraft Forge/Fabric support

## [1.1.1] - 2024-01-04

### Added
- **Death Drops**: Robots now drop spawn items with current level (Forge) or cores (Fabric) when they die
- **Level Preservation**: Spawn items retain robot progress and level information

### Changed
- **Drop System**: Different drop mechanics for Forge vs Fabric platforms

### ⚠️ Known Issues
- **Dyeing Warning**: Do not dye spawn items as this will cause loss of all progress and level data

## [1.1.0] - 2023-11-05

### Added
- **Visual Effects**: Heart particles when robots are tamed
- **Blink Animation**: Natural blinking behavior for all robot types
- **Functional Spawn Items**: Proper spawn item functionality with information display
- **Configuration System**: Config file for mod options (Work in Progress)
- **Visual Accessories**: Headphones visual indicator for mechanical robots
- **Dyeable Crafting**: Craft spawn items with dye colors

### Changed
- **Animation Improvements**: Enhanced idle, walk, and rest animations
- **Tail Animations**: Improved tail movement for Dragon and Kitsune robots
- **Spawn Recipes**: Updated crafting recipes for Bunny, Bunny2, Honey, and Vanilla robots

### Fixed
- **Information Display**: RobotCore and SpawnRobot items now show proper information
- **Sword Rendering**: Swords now render correctly when equipped
- **Dragon Wings**: Dragon robot wings now render properly

### Removed
- **Debug Messages**: Removed health and wary debug information from notifications
- **Taming Messages**: Removed message notifications when robots are tamed
- **Auto Attack Messages**: Removed notifications when toggling auto attack
- **State Messages**: Removed notifications when switching robot states

### 💡 Enhancement
- **Experience Bonus**: Named robots earn additional experience points

## [1.1b-beta] - 2023-10-13

### Added
- **Configuration File**: Initial config file implementation
- **Heart Particles**: Visual feedback when taming robots
- **Enhanced Animations**: Improved animations with blinking behavior
- **Dragon Improvements**: Better animation system for dragon robots
- **Recipe Updates**: Modified spawn recipes for core robot types

### Fixed
- **Information Display**: RobotCore and SpawnRobot items display correct information
- **Sword Rendering**: Fixed sword rendering issues
- **Dragon Wings**: Resolved dragon wing rendering problems

## [1.0.2] - 2023-09-04

### Added
- **Localization**: Message translation support for multiple languages

## [1.0.1] - 2023-08-29

### Added
- **Dragon Colors**: 16 color variations for Dragon robots
- **Kitsune Colors**: 16 color variations for Kitsune robots  
- **Neko Colors**: 16 color variations for Neko robots

### Changed
- **Color System**: Expanded color palette to 16 variations per robot type

## [1.0.0] - 2023-08-15

### Added
- **New Robot Types**: Dragon, Kitsune, and Neko robots
- **Expanded Roster**: Total of 7 robot types available

### Changed
- **Internal Structure**: Major code restructuring for better maintainability
- **Robot Variety**: Significantly expanded robot selection

## [0.3.0-beta] - 2023-07-18

### Added
- **Tooltips**: Information tooltips for better user experience
- **Wary System**: Robots become cautious when hurt or attacking, maintaining defensive posture
- **Head Animations**: Dynamic head movement animations for all robot types

### Changed
- **Gradle Configuration**: Updated build system configuration
- **Internal Code**: Major internal code restructuring
- **Default Behavior**: Robots now spawn with auto attack enabled by default

### Fixed
- **Animation Synchronization**: Proper animation order synchronization across all robots

## [0.2.5-beta] - 2023-06-22

### Added
- **Head Animations**: Dynamic head movement for all robot types

### Changed
- **Dye Consumption**: Dye items are now consumed when used for color changes
- **Base Defense Mode**: Functional base defense behavior
- **Auto Attack**: Fully functional auto attack system
- **Movement Speed**: Speed now varies based on robot state

### Fixed
- **State-Based Behavior**: Improved behavior consistency across different states

### ⚠️ Known Issues
- **Sit Animation**: Animation order issues (workaround: switch from BaseDefense to Follow state)

## [0.2.1-beta] - 2023-06-18

### Added
- **Independent Combat**: All robot types can now attack independently
- **Head Animations**: Dynamic head movement system

### Removed
- **Sit Animations**: Temporarily removed due to bug issues

### Changed
- **Combat System**: Enhanced individual robot combat capabilities

## [0.2.0-alpha] - 2023-03-02

### Added
- **Expanded Color Palette**: All robot types now support 16 colors
  - **New Colors**: White, Light Gray, Gray, Brown, Cyan, Green
  - **Applies to**: Vanilla, Honey, Bunny, and Bunny2 robots
- **Looting Information**: Enhanced item information display

### Changed
- **Texture System**: Comprehensive texture overhaul for all robot types
- **Color Consistency**: Standardized color options across all robot variants

## [0.1.2-alpha] - 2023-02-22

### Changed
- **Attribute System**: New internal attributes structure for better performance

### Fixed
- **Level Progression**: Robots can now properly level up by attacking enemies

### 🔄 Compatibility
- **Backward Compatible**: Earlier versions remain compatible with this update

## [0.1.1-alpha] - 2023-02-20

### Added
- **Complete Color System**: Bunny, Bunny2, and Honey robots now support all 10 colors
- **Statistics System**: Robot stats accessible via right-click with stick
- **Protection System**: Fire, Fall, Blast, and Projectile protection (upgradeable via right-click with book)
- **Level System**: Robots can level up to level 200
- **Robot Core Drops**: Robots drop robot cores when defeated

### Fixed
- **Level Progression**: Robots gain experience from combat

### ⚠️ Known Issues
- **Attack Leveling**: Level up by attacking not working (only damage received works)
- **Version Compatibility**: Robots from v0.0.2 won't update max level and cannot level up

## [0.0.2-alpha] - 2023-02-09

### Added
- **Vanilla Colors**: 6 additional colors (Orange, Magenta, Lime, Purple, Blue, Red)
- **Custom Attributes**: Each robot type now has unique custom attributes

### Changed
- **Black Color**: Modified Vanilla robot black color appearance

### Fixed
- **Combat System**: Robots no longer attack inappropriately
- **Sitting Behavior**: Resolved sitting animation issues

### ⚠️ Known Issues
- **Settings Persistence**: Robot settings don't load correctly when spawned (workaround: re-enter world)
- **Sitting Animation**: Animation switching issues (workaround: right-click with compass, then empty hand to set sit)

## [0.0.1-alpha] - 2023-02-06

### Added
- **Initial Robot Types**: Bunny, Bunny2, Honey, and Vanilla robots
- **Color System**: Right-click with dye to change robot colors
  - **Bunny**: Pink, Yellow, Light Blue, Purple, Red
  - **Bunny2**: Pink, Blue, Lime, Orange  
  - **Honey**: Yellow, Light Blue, Pink
  - **Vanilla**: Pink, Yellow, Light Blue, Black
- **Crafting System**: Original recipes for all robot types
- **Basic Behaviors**: Standby, tracking, and attack modes

### Technical Details
- **First Release**: Initial alpha version of LovelyRobot Reboot
- **Core Features**: Basic robot functionality and interaction system

## Migration Notes

### Upgrading to 1.0.0
- **GeckoLib Dependency**: Ensure GeckoLib is installed before updating
- **Configuration Reset**: Some configuration options have been restructured
- **World Compatibility**: Existing robot entities should migrate automatically

### Breaking Changes
- **API Changes**: Custom robot behavior APIs have been updated (affects addon developers)
- **Configuration Format**: Config file format has changed (automatic migration provided)

## Future Roadmap

### LovelyRobot: Tribute (In Development - 2025 Q1)
- Exact recreation of original mod functionality
- Support for legacy Minecraft versions (1.7.10+)
- Original color schemes and robot behaviors
- Minimal dependencies for maximum compatibility
- **Status**: Foundation complete with tlovelyr-1.20.1 implementation

### LovelyRobot: Legacy (In Development - 2025 Q1-Q2)
- Enhanced version with expanded features across multiple Minecraft versions
- Comprehensive legacy support from 1.7.10 through 1.20.1
- Standardized "Lovely" naming convention for consistency
- **Status**: Multi-version structure complete, refinement in progress
- **Challenge**: 1.12.2 version requires specialized multi-loader approach

### LovelyRobot: Reboot 2.0 (Planned 2025 Q3-Q4)
- **Robot Creator System**: Build custom robots with modular components
- **Advanced AI**: Improved robot behavior and decision-making
- **Assembly Mechanics**: Craft robot parts and assemble custom companions
- **Terminal Interface**: Advanced robot programming and control
- **Path-finding System**: Intelligent navigation and task execution
- **Frame System**: Modular robot construction framework
- **Status**: Foundation established with rlovelyr-1.20.1 structure

## Development Notes

### Current Development Approach (November 2025)
- **Multi-Version Strategy**: Comprehensive legacy support across Minecraft modding history
- **Shared Resources**: Centralized asset management for consistency across all variants
- **Naming Standardization**: "Lovely" prefix convention for improved code organization
- **Documentation-Driven**: Extensive documentation including development checklists and roadmaps

### Technical Challenges
- **Minecraft 1.12.2**: Complex version requiring specialized multi-loader approach
  - Current implementation uses workaround solution
  - Future refactoring planned for proper native Java common code architecture
- **Legacy Compatibility**: Maintaining functionality across 13+ Minecraft versions
- **Resource Management**: Balancing shared resources with version-specific requirements

### Future Architecture Plans
- **Multi-Loader Framework**: Native Java common code for shared functionality
- **Modular Design**: Component-based architecture for easier maintenance
- **Automated Build Pipeline**: Streamlined development and release process

## Known Issues

### Current Development Version (Unreleased)
- **1.12.2 Implementation**: Uses workaround approach, proper multi-loader solution in development
- **Build Complexity**: Multiple version support requires careful dependency management
- **Documentation**: Some legacy version documentation still in progress

### Current Version (1.0.0)
- **Performance**: Minor frame drops with 10+ robots in close proximity
- **Multiplayer**: Occasional desync of robot animations in high-latency environments
- **Compatibility**: Some texture packs may not display robot colors correctly

### Workarounds
- **Performance**: Limit active robots per chunk to 5-8 for optimal performance
- **Multiplayer**: Server restart resolves most synchronization issues
- **Texture Packs**: Use resource packs specifically designed for LovelyRobot

## Support and Community

- **Issues**: Report bugs at [GitHub Issues](https://github.com/heria-zone/reboot-lovely-robot/issues)
- **Discord**: Join our community at [Discord Server](https://discord.gg/KdZZMj89bU)
- **Downloads**: 
  - [CurseForge](https://www.curseforge.com/minecraft/mc-mods/reboot-lovelyrobot)
  - [Modrinth](https://modrinth.com/mod/reboot-lovelyrobot)

## Contributors

### Core Team
- **MSymbios!** - Lead Developer and Project Maintainer

### Special Thanks
- **lilacx02** - Original LovelyRobot mod creator
- **D Flog Flag & VirtualBlack8** - LovelyRobotsPE Project creators
- **Community Contributors** - Beta testers and feedback providers

## License

This project is licensed under the [MIT License](https://www.mit.edu/~amini/LICENSE.md).

---

**Note**: This changelog follows the [Keep a Changelog](https://keepachangelog.com/) format. For detailed technical changes, see the [commit history](https://github.com/heria-zone/reboot-lovely-robot/commits) on GitHub.