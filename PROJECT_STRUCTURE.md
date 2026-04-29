# JavaFX 3D Object Viewer - Project Structure

## Overview
This project has been refactored from a single large file into a well-organized, modular structure following MVC (Model-View-Controller) and separation of concerns principles.

## Package Structure

### `com.example.viewer`
- **MainApp.java** - Entry point of the application. Delegates to MainController.

### `com.example.viewer.model`
- **VaseParameters.java** - Contains all configurable parameters for the 3D vase model including dimensions, colors, and settings.

### `com.example.viewer.controller`
- **MainController.java** - Main application controller that manages the 3D scene, user interactions, and coordinates between model and view components.

### `com.example.viewer.view`
- **ControlPanel.java** - Creates and manages the UI control panel with sliders, color pickers, and toggles for all vase parameters.

### `com.example.viewer.geometry`
- **VaseMeshGenerator.java** - Generates the main vase body mesh geometry.
- **SpoutMeshGenerator.java** - Creates the spout/pouring lip mesh geometry.
- **HandleMeshGenerator.java** - Generates the handle mesh with arc-shaped geometry.
- **LidDomeMeshGenerator.java** - Creates the lid dome mesh geometry.
- **LidKnobMeshGenerator.java** - Generates the lid knob mesh geometry.

### `com.example.viewer.utils`
- **MathUtils.java** - Utility class containing mathematical helper functions like smoothStep, profile calculations, and spout weight computations.

## Key Features

### 3D Model Components
- **Vase Body**: Main container with customizable height, thickness, belly amount, and neck taper
- **Spout**: Adjustable pouring lip with length, width, and lift parameters
- **Handle**: Arc-shaped handle that attaches to the vase surface with size, position, and thickness controls
- **Lid**: Two-part lid consisting of a dome and hexagonal knob

### Interactive Controls
- **Sliders**: Real-time parameter adjustment for all dimensions
- **Color Pickers**: Individual color control for each component
- **Light Toggles**: Enable/disable ambient and point lighting
- **Mouse Controls**: Rotate view with drag, zoom with scroll wheel

### Technical Implementation
- **TriangleMesh**: Custom mesh generation for all 3D components
- **PhongMaterial**: Realistic lighting and shading
- **Property Binding**: Reactive UI updates synchronized with 3D model
- **Constraint System**: Handle position automatically constrained within vase bounds

## Build and Run

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 21

### Compilation
```bash
mvn compile
```

### Running the Application
```bash
mvn javafx:run
```

## Architecture Benefits

1. **Separation of Concerns**: Each class has a single, well-defined responsibility
2. **Modularity**: Components can be developed, tested, and modified independently
3. **Maintainability**: Code is organized logically, making it easier to understand and modify
4. **Reusability**: Geometry generators and utilities can be reused in other projects
5. **Testability**: Individual components can be unit tested in isolation

## File Organization

The project follows standard Maven structure:
- `src/main/java/` - Java source code
- `src/main/resources/` - Resource files
- `pom.xml` - Maven configuration with JavaFX dependencies

## Future Enhancements

The modular structure makes it easy to add:
- New 3D object types
- Additional mesh generators
- Enhanced UI components
- Import/export functionality
- Animation features
