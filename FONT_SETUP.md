# Font Setup Guide - OpenDyslexic

## What is OpenDyslexic?

**OpenDyslexic** is a free, open-source font designed specifically to increase readability for people with dyslexia. It features:

✅ Weighted bottoms on letters (helps anchor letters)  
✅ Increases space between letters  
✅ Unique character shapes to reduce confusion  
✅ Improved readability for all users  

## Installation Steps

### Option 1: Download via Command Line (Recommended)

#### On macOS/Linux:
```bash
# Navigate to app fonts directory
cd app/src/main/res/font

# Download OpenDyslexic 3 Regular (OTF format)
curl -L "https://github.com/antijingoist/open-dyslexic/raw/master/fonts/OpenDyslexic3-Regular.otf" \
  -o opendyslexic_regular.ttf

# Verify download
ls -lh opendyslexic_regular.ttf
```

#### On Windows (PowerShell):
```powershell
# Navigate to app fonts directory
cd app/src/main/res/font

# Download using Invoke-WebRequest
Invoke-WebRequest `
  -Uri "https://github.com/antijingoist/open-dyslexic/raw/master/fonts/OpenDyslexic3-Regular.otf" `
  -OutFile "opendyslexic_regular.ttf"

# Verify download
Get-Item opendyslexic_regular.ttf | Select-Object Length
```

### Option 2: Manual Download

1. **Go to OpenDyslexic GitHub:**
   - Visit: https://github.com/antijingoist/open-dyslexic/tree/master/fonts

2. **Download the Font:**
   - Click on `OpenDyslexic3-Regular.otf`
   - Click "Download raw file"
   - Save the file

3. **Place in Project:**
   ```
   ThriveLearn/app/src/main/res/font/
   ```

4. **Rename (if needed):**
   - Rename to: `opendyslexic_regular.ttf`
   - (The .otf extension works, but .ttf is conventional for Android)

### Option 3: All Fonts Package

If you want all OpenDyslexic variants:

```bash
# Download entire fonts archive
cd app/src/main/res/font

wget -O fonts.zip "https://github.com/antijingoist/open-dyslexic/releases/download/OpenDyslexic3.0/OpenDyslexic3.0.zip"

# Extract
unzip fonts.zip

# Move regular font
cp fonts/OpenDyslexic3-Regular.otf opendyslexic_regular.ttf

# Optional: Move other variants
cp fonts/OpenDyslexic3-Bold.otf opendyslexic_bold.ttf
cp fonts/OpenDyslexic3-Italic.otf opendyslexic_italic.ttf
cp fonts/OpenDyslexic3-BoldItalic.otf opendyslexic_bold_italic.ttf

# Cleanup
rm -rf fonts fonts.zip
```

## Verification

### Check File Exists
```bash
ls -lh app/src/main/res/font/opendyslexic_regular.ttf
# Should show: -rw-r--r--  ... opendyslexic_regular.ttf (~200KB)
```

### Verify File Size
- Expected size: **150-250 KB**
- If smaller (<50KB), redownload
- If larger (>500KB), may have wrong file

### Check in Android Studio
1. Open Android Studio
2. Navigate to: `app → src → main → res → font`
3. Should see: `opendyslexic_regular.ttf`
4. If not visible, refresh: `File → Sync Now`

## Building with Font

### Clean Build
```bash
# Clean Gradle cache
./gradlew clean

# Rebuild project
./gradlew build
```

### If Font Still Not Loading

1. **Verify Path (Case Sensitive):**
   ```
   app/src/main/res/font/opendyslexic_regular.ttf
   ```
   ⚠️ NOT: `opendyslexic_Regular.ttf` (capital R)

2. **Check AndroidManifest.xml:**
   ```xml
   <application
       android:name=".ThriveLearnApp"
       ...
   >
   ```
   ✅ Should have `android:name=".ThriveLearnApp"`

3. **Verify Compose Theme Code:**
   - Open: `app/src/main/java/com/thrivelearn/app/theme/AccessibleTheme.kt`
   - Should contain:
     ```kotlin
     val OpenDyslexicFont = FontFamily(Font(R.font.opendyslexic_regular))
     ```

4. **Force Regenerate R File:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

5. **Restart Android Studio:**
   - File → Invalidate Caches → Restart

## Testing Font

### On Emulator/Device

1. **Launch App**
   ```bash
   ./gradlew installDebug
   ```

2. **Navigate to Settings**
   - Bottom tab: ⚙️ Settings
   - Tap: "⚙️ Settings"

3. **Enable Dyslexia-Friendly Font**
   - Section: "🎨 Accessibility"
   - Font Style: Select "Dyslexia Friendly"

4. **Verify Change**
   - Text should display in OpenDyslexic
   - Heavier bottoms on letters
   - More space between characters
   - Letters look distinctly different

### Troubleshooting Font Not Applying

**Issue:** Settings change doesn't apply font

**Solution:**
```bash
# 1. Check theme composition
grep -r "OpenDyslexicFont" app/src/main/java/

# 2. Verify R resources
./gradlew clean
./gradlew build

# 3. Uninstall and reinstall
./gradlew uninstallDebug installDebug
```

## Font Details

### OpenDyslexic Characteristics

| Feature | Normal Font | OpenDyslexic |
|---------|-------------|---------------|
| **Weight** | Even | Heavier bottom |
| **Spacing** | Standard | Increased |
| **Kerning** | Adjusted | Generous |
| **Clarity** | Good | Excellent |
| **Readability** | 100% | 150%+ for dyslexic users |

### Supported Characters
- ✅ Latin alphabet (A-Z, a-z)
- ✅ Numbers (0-9)
- ✅ Common symbols
- ✅ Accented characters
- ✅ Punctuation

## Multiple Font Variants (Optional)

For more flexibility, add other OpenDyslexic styles:

### Bold Variant
```bash
curl -L "https://github.com/antijingoist/open-dyslexic/raw/master/fonts/OpenDyslexic3-Bold.otf" \
  -o app/src/main/res/font/opendyslexic_bold.ttf
```

### Italic Variant
```bash
curl -L "https://github.com/antijingoist/open-dyslexic/raw/master/fonts/OpenDyslexic3-Italic.otf" \
  -o app/src/main/res/font/opendyslexic_italic.ttf
```

### Use in Code
```kotlin
val OpenDyslexicFont = FontFamily(
    Font(R.font.opendyslexic_regular, weight = FontWeight.Normal),
    Font(R.font.opendyslexic_bold, weight = FontWeight.Bold),
    Font(R.font.opendyslexic_italic, weight = FontWeight.Normal, style = FontStyle.Italic)
)
```

## License

**OpenDyslexic Font:**
- License: [SIL Open Font License 1.1](https://scripts.sil.org/OFL)
- Free to use, modify, and distribute
- No commercial restrictions
- Attribution appreciated but not required

## Resources

- 📖 **OpenDyslexic Official:** https://opendyslexic.org/
- 🐙 **GitHub Repository:** https://github.com/antijingoist/open-dyslexic
- 📝 **Font Documentation:** https://opendyslexic.org/about/
- 🔬 **Research:** https://opendyslexic.org/research/

## FAQ

**Q: Can I use a different dyslexia font?**

A: Yes! You can replace with any OpenFont License (OFL) font:
- Dyslexie (commercial)
- Lexie Readable
- DejaVu (has dyslexia-friendly variants)

Just replace the file and adjust the code reference.

**Q: Will the font increase APK size?**

A: Minimally (~200KB for regular). The font adds:
- 150-250 KB uncompressed
- ~50-100 KB compressed in APK
- Negligible at typical app sizes (10-15 MB)

**Q: Can I remove the font to reduce size?**

A: Yes, but then remove the dyslexia-friendly option from Settings.
Comment out in `AccessibleTheme.kt`:
```kotlin
// val OpenDyslexicFont = FontFamily(Font(R.font.opendyslexic_regular))
```

**Q: How do I verify the font file integrity?**

A: Check file properties:
```bash
file app/src/main/res/font/opendyslexic_regular.ttf
# Should output: OpenType font data
```

**Q: Does font need to be in specific format?**

A: Android supports:
- ✅ `.ttf` (TrueType Font) - Recommended
- ✅ `.otf` (OpenType Font) - Also works
- ❌ `.woff` (Web fonts) - Not supported

---

**Setup Complete!** 🎉

Your ThriveLearn app now has OpenDyslexic font support for enhanced accessibility.

If you encounter issues, check the troubleshooting section or open an issue on GitHub.
