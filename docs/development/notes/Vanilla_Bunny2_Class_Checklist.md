# Vanilla & Bunny2 Robot Implementation - Class Checklist

**Purpose**: Track progress of porting classes from archived 1.21.1 and 1.20.4 codebases to Legacy 1.20.1
**Sprint**: SPRINT_01
**Created**: 2025-11-22
**Target**: `sources/legacy/llovelyr-1.20.1/`

---

## Reference Codebases

- **Primary Reference**: `archive/1.21.X/rlovelyr-1.21.1/Forge/` (Most modern architecture)
- **Secondary Reference**: `archive/1.20.X/rlovelyr-1.20.4/Forge/` (Version-specific patterns)
- **Target Implementation**: `sources/legacy/llovelyr-1.20.1/`

---

## Core Classes (Priority: CRITICAL)

### Main Mod Class
- [ ] `LovelyRobot.java` - Main mod initialization class

### Configuration System
- [ ] `config/LovelyRobotConfig.java` - Configuration management
- [ ] `config/LovelyRobotID.java` - ID constants and registry names
- [ ] `config/LovelyRobotResource.java` - Resource location management

---

## Entity System (Priority: CRITICAL)

### Entity Registration
- [ ] `entity/LovelyRobotEntities.java` - Entity type registration

### Base Entity Classes (Internal)
- [ ] `entity/internal/RobotEntity.java` - Base robot entity class (CRITICAL - implement first)
- [ ] `entity/internal/NativeEntityType.java` - Entity type wrapper

### Robot Entity Implementations (Focus: Vanilla & Bunny2)
- [ ] `entity/custom/VanillaEntity.java` - **PRIMARY TARGET**
- [ ] `entity/custom/Bunny2Entity.java` - **PRIMARY TARGET**
- [ ] `entity/custom/BunnyEntity.java` - (Future)
- [ ] `entity/custom/HoneyEntity.java` - (Future)
- [ ] `entity/custom/DragonEntity.java` - (Future)
- [ ] `entity/custom/KitsuneEntity.java` - (Future)
- [ ] `entity/custom/NekoEntity.java` - (Future)
- [ ] `entity/custom/PrimeEntity.java` - (Future)
- [ ] `entity/custom/EmpyriumEntity.java` - (Future)
- [ ] `entity/custom/HyperionEntity.java` - (Future)

### Entity Enums
- [x] `entity/internal/enums/EntityAnimation.java` - Animation state enum ✓ 2025-11-22
- [x] `entity/internal/enums/EntityAnimator.java` - Animator configuration ✓ 2025-11-22
- [x] `entity/internal/enums/EntityHand.java` - Hand/equipment enum ✓ 2025-11-22
- [x] `entity/internal/enums/EntityModel.java` - Model type enum ✓ 2025-11-22
- [x] `entity/internal/enums/EntityTexture.java` - Texture variant enum ✓ 2025-11-22
- [x] `entity/internal/enums/EntityVariant.java` - Robot variant enum ✓ 2025-11-22

---

## AI Goals (Priority: HIGH)

### Custom Goals
- [ ] `entity/goal/AiFollowOwnerGoal.java` - Follow owner behavior
- [ ] `entity/goal/AiAutoAttackGoal.java` - Auto attack behavior
- [ ] `entity/goal/AiBaseDefenseGoal.java` - Base defense behavior

---

## Client Rendering (Priority: HIGH)

### Models (GeckoLib)
- [ ] `entity/client/model/VanillaModel.java` - **PRIMARY TARGET**
- [ ] `entity/client/model/Bunny2Model.java` - **PRIMARY TARGET**
- [ ] `entity/client/model/BunnyModel.java` - (Future)
- [ ] `entity/client/model/HoneyModel.java` - (Future)
- [ ] `entity/client/model/DragonModel.java` - (Future)
- [ ] `entity/client/model/KitsuneModel.java` - (Future)
- [ ] `entity/client/model/NekoModel.java` - (Future)
- [ ] `entity/client/model/PrimeModel.java` - (Future)
- [ ] `entity/client/model/EmpyriumModel.java` - (Future)
- [ ] `entity/client/model/HyperionModel.java` - (Future)

### Renderers
- [ ] `entity/client/renderer/VanillaRenderer.java` - **PRIMARY TARGET**
- [ ] `entity/client/renderer/Bunny2Renderer.java` - **PRIMARY TARGET**
- [ ] `entity/client/renderer/BunnyRenderer.java` - (Future)
- [ ] `entity/client/renderer/HoneyRenderer.java` - (Future)
- [ ] `entity/client/renderer/DragonRenderer.java` - (Future)
- [ ] `entity/client/renderer/KitsuneRenderer.java` - (Future)
- [ ] `entity/client/renderer/NekoRenderer.java` - (Future)
- [ ] `entity/client/renderer/PrimeRenderer.java` - (Future)
- [ ] `entity/client/renderer/EmpyriumRenderer.java` - (Future)
- [ ] `entity/client/renderer/HyperionRenderer.java` - (Future)

### Render Layers
- [ ] `entity/client/layer/VanillaLayer.java` - **PRIMARY TARGET**
- [ ] `entity/client/layer/Bunny2Layer.java` - **PRIMARY TARGET**
- [ ] `entity/client/layer/BunnyLayer.java` - (Future)
- [ ] `entity/client/layer/HoneyLayer.java` - (Future)
- [ ] `entity/client/layer/DragonLayer.java` - (Future)
- [ ] `entity/client/layer/KitsuneLayer.java` - (Future)
- [ ] `entity/client/layer/NekoLayer.java` - (Future)
- [ ] `entity/client/layer/PrimeLayer.java` - (Future)
- [ ] `entity/client/layer/EmpyriumLayer.java` - (Future)
- [ ] `entity/client/layer/HyperionLayer.java` - (Future)

---

## Item System (Priority: HIGH)

### Item Registration
- [ ] `item/LovelyRobotItems.java` - Item registration
- [ ] `item/LovelyRobotItemsGroup.java` - Creative tab/group

### Custom Items
- [ ] `item/custom/SpawnItem.java` - Robot spawn items
- [ ] `item/custom/RobotCoreItem.java` - Robot core items (drops on death)

### Item Utilities
- [ ] `item/util/TooltipUtils.java` - Tooltip formatting utilities

---

## Common/Shared Systems (Priority: CRITICAL)

### Entity Abstractions
- [ ] `common/entity/InternalEntity.java` - Base entity abstraction
- [ ] `common/entity/InternalEntityType.java` - Entity type abstraction
- [ ] `common/entity/InternalAnimation.java` - Animation system abstraction
- [ ] `common/entity/InternalModel.java` - Model system abstraction
- [ ] `common/entity/InternalLayer.java` - Render layer abstraction
- [ ] `common/entity/InternalLogic.java` - Logic/behavior abstraction
- [ ] `common/entity/InternalParticle.java` - Particle system abstraction

### Entity Enums (Common)
- [x] `common/entity/enums/EntityState.java` - Entity state enum ✓ 2025-11-22

### Utilities
- [ ] `common/util/ObjectUtil.java` - Object utility methods
- [ ] `common/util/internal/Utility.java` - General utility methods
- [ ] `common/util/internal/Version.java` - Version management
- [ ] `common/util/interfaces/IReadWriteNBT.java` - NBT serialization interface

---

## Event System (Priority: MEDIUM)

### Event Handlers
- [ ] `event/LovelyRobotEvents.java` - Mod event handlers

---

## Component System (Priority: MEDIUM)

### Components (1.21.1 only - may need adaptation for 1.20.1)
- [ ] `component/LovelyRobotComponent.java` - Data component system (check if needed in 1.20.1)

---

## Block System (Priority: LOW - 1.20.4 only)

### Blocks (Future feature)
- [ ] `block/LovelyRobotBlocks.java` - Block registration (1.20.4 has this, 1.21.1 doesn't)

---

## Implementation Priority Order

### Phase 1: Foundation (Week 1, Days 1-2)
1. Configuration system (3 classes)
2. Common utilities and interfaces (8 classes)
3. Entity enums (7 classes)
4. Main mod class (1 class)

### Phase 2: Core Entity System (Week 1, Days 3-4)
1. Entity registration (1 class)
2. Base robot entity (2 classes)
3. AI goals (3 classes)
4. Event handlers (1 class)

### Phase 3: Vanilla Robot (Week 1, Days 5-7)
1. VanillaEntity (1 class)
2. VanillaModel (1 class)
3. VanillaRenderer (1 class)
4. VanillaLayer (1 class)

### Phase 4: Bunny2 Robot (Week 2, Days 1-3)
1. Bunny2Entity (1 class)
2. Bunny2Model (1 class)
3. Bunny2Renderer (1 class)
4. Bunny2Layer (1 class)

### Phase 5: Item System (Week 2, Days 4-5)
1. Item registration (2 classes)
2. Spawn items (1 class)
3. Robot core items (1 class)
4. Tooltip utilities (1 class)

### Phase 6: Testing & Polish (Week 2, Days 6-7)
1. Integration testing
2. Bug fixes
3. Documentation updates

---

## Class Count Summary

### Total Classes in Reference Codebases
- **1.21.1**: 67 classes
- **1.20.4**: 68 classes (includes LovelyRobotBlocks.java)

### Classes for Vanilla & Bunny2 Implementation
- **Critical Priority**: 25 classes
- **High Priority**: 10 classes
- **Medium Priority**: 2 classes
- **Low Priority**: 1 class (blocks - future)
- **Future Implementation**: 24 classes (other robot types)

### Sprint 01 Target
- **Must Implement**: 35 classes (Critical + High priority)
- **Should Implement**: 2 classes (Medium priority)
- **Total Sprint Goal**: 37 classes

---

## Progress Tracking

### Overall Progress
- **Completed**: 7 / 37 (19%)
- **In Progress**: 0
- **Blocked**: 0
- **Not Started**: 30

### By Category
- **Configuration**: 0 / 3 (0%)
- **Common/Utilities**: 1 / 8 (13%)
- **Entity Core**: 6 / 10 (60%)
- **AI Goals**: 0 / 3 (0%)
- **Vanilla Robot**: 0 / 4 (0%)
- **Bunny2 Robot**: 0 / 4 (0%)
- **Item System**: 0 / 5 (0%)

---

## Notes

### Key Differences Between Versions
- **1.21.1**: Has component system, no blocks
- **1.20.4**: Has blocks, no component system
- **1.20.1 Target**: Need to determine which features to include

### Implementation Strategy
1. Start with common/shared systems
2. Build foundation before specific robots
3. Implement Vanilla first (simpler)
4. Use Vanilla as template for Bunny2
5. Test thoroughly at each phase

### Code Style Reminder
- Follow `.kiro/steering/project-coding-style.md` exactly
- Use JavaDoc comments on all public classes/methods
- Include section headers and closing comments
- Maintain consistent package structure

---

**Last Updated**: 2025-11-22
**Next Review**: Daily during sprint
