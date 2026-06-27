# Contributing to ThriveLearn

We welcome contributions from developers, accessibility experts, and users passionate about inclusive technology!

## How to Contribute

### 1. Reporting Issues

**Found a bug?**

1. Check [existing issues](https://github.com/Teja7876/ThriveLearn/issues)
2. Create a new issue with:
   - Clear title describing the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Device/Android version info
   - Screenshots if applicable

**Example:**
```
Title: Dictation doesn't work on Android 12

Steps:
1. Grant microphone permission
2. Tap "Dictate" button
3. Speak clearly

Expected: Text appears in input field
Actual: Nothing happens, no error

Device: Pixel 5
Android: 12
```

### 2. Suggesting Features

**Have an idea?**

1. Check [discussions](https://github.com/Teja7876/ThriveLearn/discussions)
2. Start a discussion with:
   - Feature name
   - Problem it solves
   - Proposed solution
   - Benefit for PwD users

### 3. Pull Requests

**Setup Development Environment:**

```bash
# Clone repo
git clone https://github.com/Teja7876/ThriveLearn.git
cd ThriveLearn

# Create feature branch
git checkout -b feature/your-feature-name

# Make changes
# ...

# Commit with clear messages
git commit -am "Add: Feature description"

# Push and create PR
git push origin feature/your-feature-name
```

**PR Guidelines:**
- ✅ Link related issues
- ✅ Add tests if applicable
- ✅ Update README if needed
- ✅ Follow code style (ktlint)
- ✅ Test on Android 26+
- ✅ Verify accessibility compliance

## Code Style

### Kotlin Style Guide

```kotlin
// Use meaningful names
val speechRecognizer: SpeechRecognizer

// Proper spacing
fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)

// Documentation
/**
 * Starts listening for speech input.
 * @param onResult Callback when speech is recognized
 * @param onError Callback on error
 */
fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)

// Error handling
try {
    // code
} catch (e: Exception) {
    e.printStackTrace()
    onError(e.message ?: "Unknown error")
}
```

### Compose Guidelines

```kotlin
// Use descriptive parameter names
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
)

// Always add semantics
Button(
    onClick = onClick,
    modifier = modifier.semantics {
        contentDescription = "Clear all notes"
    }
)
```

## Areas for Contribution

### 🎨 **UI/Accessibility Improvements**
- [ ] Improve WCAG AAA compliance
- [ ] Add more theme options
- [ ] Enhance screen reader support
- [ ] Support gesture navigation
- [ ] Add haptic feedback

### 🧠 **Features**
- [ ] Full-text search in notes
- [ ] Export to PDF
- [ ] Collaborative notes
- [ ] Cloud sync
- [ ] Drawing tools
- [ ] Custom keyboard shortcuts

### 🌍 **Localization**
- [ ] Translate to Spanish
- [ ] Translate to Hindi
- [ ] Translate to French
- [ ] Right-to-left language support

### 🧪 **Testing**
- [ ] Add unit tests
- [ ] Add UI tests
- [ ] Add integration tests
- [ ] Test accessibility
- [ ] Device compatibility testing

### 📚 **Documentation**
- [ ] API documentation
- [ ] Architecture guide
- [ ] User tutorials
- [ ] Video guides
- [ ] Accessibility guide

### 🐛 **Bug Fixes**
Check [open issues](https://github.com/Teja7876/ThriveLearn/issues) for bugs to fix.

## Testing Your Changes

### Local Testing
```bash
# Build debug APK
./gradlew installDebug

# Run tests
./gradlew test

# Check code style
./gradlew ktlintCheck
```

### Accessibility Testing
- Enable TalkBack (Settings > Accessibility > TalkBack)
- Test with keyboard navigation
- Verify screen reader compatibility
- Test high contrast mode
- Check dyslexia-friendly font

### Device Testing
- Test on Android 26 (minimum)
- Test on Android 34 (target)
- Test on various devices
- Test with different screen sizes

## Commit Message Guide

```
Type: Short description (max 50 chars)

Longer explanation if needed (max 72 chars per line)

- More detailed points
- About the change
- With examples

Fixes #123
```

**Types:**
- `Add:` - New feature
- `Fix:` - Bug fix
- `Improve:` - Enhancement
- `Refactor:` - Code restructuring
- `Docs:` - Documentation
- `Test:` - Add tests
- `Remove:` - Delete code

**Example:**
```
Add: Voice rate control in settings

Implement adjustable speech rate slider in settings screen.
Users can now control speech playback speed from 0.5x to 2.0x.

Fixes #45
```

## License Agreement

By contributing, you agree that your contributions will be licensed under the MIT License.

## Code of Conduct

- Be respectful and inclusive
- Focus on accessibility
- Help each other learn
- No harassment or discrimination
- Welcome all skill levels

## Questions?

- 💬 [GitHub Discussions](https://github.com/Teja7876/ThriveLearn/discussions)
- 🐛 [GitHub Issues](https://github.com/Teja7876/ThriveLearn/issues)
- 📧 Email: Teja7876@gmail.com

Thank you for making ThriveLearn more accessible! 🙏
