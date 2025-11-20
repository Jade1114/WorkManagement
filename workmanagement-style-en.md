# Work Management System Style Development Guide

## 1. Overview

This style guide defines the complete frontend design specifications for the work management system, including design principles, spacing system, color system, typography system, border radius and shadows, component styles, interactive animations, and responsive layouts. The system is built using **Vue 3** and **Element Plus**, aiming to provide a clean, modern, unified, and easily maintainable user interface.

**Intended for**: Frontend developers, UI designers, backend developers (understanding frontend constraints)

---

## 2. Design Principles

- **Clean and Modern**: Adopt a minimalist style, emphasize functionality, reduce visual burden
- **Consistency**: Colors, spacing, fonts, and animations remain consistent to ensure visual coherence
- **Intuitive and Easy to Use**: Clear information hierarchy, explicit visual feedback, reduce learning curve
- **Responsive First**: Mobile-first approach, progressively adapted to tablets and desktops
- **Accessibility**: Consider colorblindness, keyboard navigation, screen readers, and other accessibility needs
- **Maintainability**: Use design systems rather than hardcoded values for easier maintenance and upgrades

---

## 3. Design System Foundations

### 3.1 Base Spacing System (8px Grid)

Adopts **8px base unit** to establish a unified spacing system. All margin and padding values must be multiples of 4px or 8px.

| Level | Pixel Value | CSS Variable | Use Case |
|-------|-------------|--------------|----------|
| XS | 4px | `--spacing-xs` | Compact element spacing (tags, badges) |
| S | 8px | `--spacing-s` | Component internal spacing, form item spacing |
| M | 16px | `--spacing-m` | Card internal padding, group spacing |
| L | 24px | `--spacing-l` | Large container padding, main area spacing |
| XL | 32px | `--spacing-xl` | Section spacing, main content area padding |
| 2XL | 40px | `--spacing-2xl` | Top-level page spacing |

```css
:root {
  --spacing-xs: 4px;
  --spacing-s: 8px;
  --spacing-m: 16px;
  --spacing-l: 24px;
  --spacing-xl: 32px;
  --spacing-2xl: 40px;
}

/* Usage example */
.card {
  padding: var(--spacing-l);
  margin-bottom: var(--spacing-xl);
}

.form-item {
  margin-bottom: var(--spacing-m);
}
```

---

## 4. Color Scheme

### 4.1 Core Colors

| Color Name | Hex | CSS Variable | Purpose |
|------------|-----|--------------|---------|
| Page Background | `#f5f7fa` | `--color-bg-page` | Main page background |
| Card Background | `#ffffff` | `--color-bg-card` | Cards, modals, menus |
| Border Color | `#dcdfe6` | `--color-border` | Borders, dividers |
| Text - Primary | `#303133` | `--color-text-primary` | Titles, body text |
| Text - Secondary | `#606266` | `--color-text-secondary` | Description text, legends |
| Text - Tertiary | `#909399` | `--color-text-tertiary` | Disabled text, placeholders, hints |
| Text - Disabled | `#c0c4cc` | `--color-text-disabled` | Completely disabled text |

### 4.2 Functional Colors

| Function | Hex | CSS Variable | Purpose |
|----------|-----|--------------|---------|
| Primary | `#409eff` | `--color-primary` | Primary buttons, links, title highlights |
| Success | `#67c23a` | `--color-success` | Success messages, confirmation actions |
| Warning | `#e6a23c` | `--color-warning` | Warning messages, cautious actions |
| Error | `#f56c6c` | `--color-error` | Error messages, delete actions |
| Info | `#909399` | `--color-info` | Neutral messages, explanatory information |

### 4.3 Color Application Rules

```css
:root {
  --color-bg-page: #f5f7fa;
  --color-bg-card: #ffffff;
  --color-border: #dcdfe6;
  --color-text-primary: #303133;
  --color-text-secondary: #606266;
  --color-text-tertiary: #909399;
  --color-text-disabled: #c0c4cc;
  --color-primary: #409eff;
  --color-success: #67c23a;
  --color-warning: #e6a23c;
  --color-error: #f56c6c;
  --color-info: #909399;
}

/* Link colors */
a {
  color: var(--color-primary);
  text-decoration: none;
}

a:hover {
  color: #66b1ff;
}

a:visited {
  color: #836eff;
}

a:disabled {
  color: var(--color-text-disabled);
  cursor: not-allowed;
}
```

---

## 5. Typography System

### 5.1 Font Size System (6-Level System)

| Level | Size | Line Height | Weight | Use Case |
|-------|------|-------------|--------|----------|
| H1 | 28px | 1.5 | 600 | Page titles |
| H2 | 24px | 1.5 | 600 | Primary section titles |
| H3 | 20px | 1.5 | 600 | Secondary section titles |
| H4 | 16px | 1.5 | 600 | Tertiary section titles |
| Body | 14px | 1.6 | 400 | Body text, card content |
| Small | 12px | 1.5 | 400 | Description text, tables, legends |

### 5.2 Font Stack

```css
:root {
  --font-family-base: "Helvetica Neue", -apple-system, BlinkMacSystemFont, "Segoe UI", 
                      Roboto, "PingFang SC", sans-serif;
  --font-family-mono: "Courier New", "Monaco", monospace;
}

body {
  font-family: var(--font-family-base);
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-primary);
}

h1 {
  font-size: 28px;
  line-height: 1.5;
  font-weight: 600;
  margin: var(--spacing-xl) 0 var(--spacing-l) 0;
}

h2 {
  font-size: 24px;
  line-height: 1.5;
  font-weight: 600;
  margin: var(--spacing-l) 0 var(--spacing-m) 0;
}

h3 {
  font-size: 20px;
  line-height: 1.5;
  font-weight: 600;
  margin: var(--spacing-m) 0 var(--spacing-s) 0;
}

h4 {
  font-size: 16px;
  line-height: 1.5;
  font-weight: 600;
  margin: var(--spacing-m) 0 var(--spacing-s) 0;
}

p {
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 var(--spacing-m) 0;
}

small {
  font-size: 12px;
  line-height: 1.5;
  color: var(--color-text-tertiary);
}

code, pre {
  font-family: var(--font-family-mono);
}
```

---

## 6. Border Radius System

Adopts **4-level border radius specification**, selecting appropriate radius values based on component characteristics.

| Level | Pixel Value | CSS Variable | Use Case |
|-------|-------------|--------------|----------|
| None | 0px | `--radius-none` | Square components |
| Compact | 4px | `--radius-compact` | Tags, badges, small buttons |
| Standard | 8px | `--radius-standard` | Cards, buttons, input fields |
| Loose | 16px | `--radius-loose` | Large cards, modals |

```css
:root {
  --radius-none: 0px;
  --radius-compact: 4px;
  --radius-standard: 8px;
  --radius-loose: 16px;
}

.card {
  border-radius: var(--radius-standard);
}

.el-button {
  border-radius: var(--radius-standard);
}

.el-input__inner {
  border-radius: var(--radius-compact);
}

.el-tag {
  border-radius: var(--radius-compact);
}

.el-dialog {
  border-radius: var(--radius-loose);
}
```

---

## 7. Shadow System

Adopts **4-level shadow system** to express different depths and layer relationships.

| Level | Shadow Definition | CSS Variable | Use Case |
|-------|------------------|--------------|----------|
| Subtle | `0 1px 2px rgba(0,0,0,0.05)` | `--shadow-subtle` | Hover states |
| Standard | `0 2px 4px rgba(0,0,0,0.1)` | `--shadow-standard` | Cards, buttons |
| Deep | `0 4px 8px rgba(0,0,0,0.15)` | `--shadow-deep` | Overlays, menus |
| Focus | `0 0 0 2px #fff, 0 0 0 4px #409eff` | `--shadow-focus` | Focus states |

```css
:root {
  --shadow-subtle: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-standard: 0 2px 4px rgba(0, 0, 0, 0.1);
  --shadow-deep: 0 4px 8px rgba(0, 0, 0, 0.15);
  --shadow-focus: 0 0 0 2px #ffffff, 0 0 0 4px #409eff;
}

.card {
  box-shadow: var(--shadow-standard);
}

.card:hover {
  box-shadow: var(--shadow-deep);
}

.el-dropdown-menu,
.el-dialog {
  box-shadow: var(--shadow-deep);
}

.el-button:focus-visible {
  outline: none;
  box-shadow: var(--shadow-focus);
}
```

---

## 8. Form Component Styles

### 8.1 Input Fields

```css
.el-input__inner {
  height: 36px;
  padding: 0 var(--spacing-m);
  font-size: 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-compact);
  transition: all 0.3s ease;
  background-color: var(--color-bg-card);
  color: var(--color-text-primary);
}

.el-input__inner::placeholder {
  color: var(--color-text-tertiary);
}

.el-input__inner:hover {
  border-color: #b3d8ff;
}

.el-input__inner:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  outline: none;
}

.el-input__inner:disabled {
  background-color: #f5f7fa;
  color: var(--color-text-disabled);
  cursor: not-allowed;
}

.el-form-item.is-error .el-input__inner {
  border-color: var(--color-error);
}

.el-form-item.is-error .el-input__inner:focus {
  box-shadow: 0 0 0 2px rgba(245, 108, 108, 0.2);
}
```

### 8.2 Form Items

```css
.el-form-item {
  margin-bottom: var(--spacing-m);
}

.el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-s);
  line-height: 1.5;
}

.el-form-item__label.is-required::before {
  content: "*";
  color: var(--color-error);
  margin-right: 4px;
}

.el-form-item__error {
  font-size: 12px;
  color: var(--color-error);
  line-height: 1;
  margin-top: 4px;
}
```

### 8.3 Radio Buttons and Checkboxes

```css
.el-radio,
.el-checkbox {
  margin-right: var(--spacing-l);
}

.el-radio__inner,
.el-checkbox__inner {
  border: 2px solid var(--color-border);
  background-color: var(--color-bg-card);
  transition: all 0.3s ease;
}

.el-radio__inner {
  border-radius: 50%;
}

.el-checkbox__inner {
  border-radius: var(--radius-compact);
}

.el-radio__input.is-checked .el-radio__inner,
.el-checkbox__input.is-checked .el-checkbox__inner {
  border-color: var(--color-primary);
  background-color: var(--color-primary);
}

.el-radio:hover .el-radio__inner,
.el-checkbox:hover .el-checkbox__inner {
  border-color: #b3d8ff;
}

.el-radio.is-disabled .el-radio__inner,
.el-checkbox.is-disabled .el-checkbox__inner {
  background-color: #f5f7fa;
  border-color: var(--color-border);
  cursor: not-allowed;
}
```

---

## 9. Button Styles

```css
.el-button {
  height: 36px;
  padding: 0 var(--spacing-l);
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-standard);
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

/* Primary button */
.el-button--primary {
  background-color: var(--color-primary);
  color: white;
  box-shadow: var(--shadow-standard);
}

.el-button--primary:hover {
  background-color: #66b1ff;
  box-shadow: var(--shadow-deep);
}

.el-button--primary:active {
  background-color: #3a8ee6;
}

/* Success button */
.el-button--success {
  background-color: var(--color-success);
  color: white;
}

.el-button--success:hover {
  background-color: #85ce61;
}

/* Warning button */
.el-button--warning {
  background-color: var(--color-warning);
  color: white;
}

.el-button--warning:hover {
  background-color: #ebb563;
}

/* Danger button */
.el-button--danger {
  background-color: var(--color-error);
  color: white;
}

.el-button--danger:hover {
  background-color: #f78989;
}

/* Text button */
.el-button--text {
  background-color: transparent;
  color: var(--color-primary);
  box-shadow: none;
}

.el-button--text:hover {
  color: #66b1ff;
  background-color: rgba(64, 158, 255, 0.08);
}

/* Disabled state */
.el-button:disabled,
.el-button.is-disabled {
  opacity: 0.6;
  color: var(--color-text-disabled);
  background-color: #f5f7fa;
  border-color: var(--color-border);
  cursor: not-allowed;
  box-shadow: none;
}

/* Loading state */
.el-button.is-loading {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Focus state */
.el-button:focus-visible {
  outline: none;
  box-shadow: var(--shadow-focus);
}
```

---

## 10. Cards and Containers

```css
.card {
  background-color: var(--color-bg-card);
  border-radius: var(--radius-standard);
  box-shadow: var(--shadow-standard);
  padding: var(--spacing-l);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: var(--shadow-deep);
}

.el-dialog {
  border-radius: var(--radius-loose);
  box-shadow: var(--shadow-deep);
}

.el-dialog__header {
  border-bottom: 1px solid var(--color-border);
  padding: var(--spacing-l);
}

.el-dialog__body {
  padding: var(--spacing-l);
}

.el-dialog__footer {
  border-top: 1px solid var(--color-border);
  padding: var(--spacing-l);
  text-align: right;
}
```

---

## 11. Table Styles

```css
.el-table {
  background-color: var(--color-bg-card);
  border-radius: var(--radius-standard);
  box-shadow: var(--shadow-standard);
  overflow: hidden;
}

.el-table__header {
  background-color: #f5f7fa;
}

.el-table__body tr:hover > td {
  background-color: #f9f9fb !important;
}

.el-table__body-wrapper {
  border-radius: 0 0 var(--radius-standard) var(--radius-standard);
}

.el-table td {
  padding: var(--spacing-m);
  border-color: var(--color-border);
}

.el-table th {
  padding: var(--spacing-m);
  border-color: var(--color-border);
  font-weight: 600;
}
```

---

## 12. Menus and Navigation

```css
.el-menu {
  border-right: none;
  background-color: var(--color-bg-card);
}

.el-menu-item {
  height: 36px;
  line-height: 36px;
  padding: 0 var(--spacing-l);
  color: var(--color-text-primary);
  transition: all 0.3s ease;
}

.el-menu-item:hover {
  background-color: #f0f0f0;
  color: var(--color-primary);
}

.el-menu-item.is-active {
  background-color: rgba(64, 158, 255, 0.1);
  color: var(--color-primary);
  font-weight: 600;
  border-right: 3px solid var(--color-primary);
}

.el-submenu__title {
  height: 36px;
  line-height: 36px;
  padding: 0 var(--spacing-l);
  color: var(--color-text-primary);
  transition: all 0.3s ease;
}

.el-submenu__title:hover {
  background-color: #f0f0f0;
  color: var(--color-primary);
}

.el-submenu.is-active > .el-submenu__title {
  color: var(--color-primary);
  font-weight: 600;
}
```

---

## 13. Notifications and States

### 13.1 Message Notifications

```css
.el-message {
  border-radius: var(--radius-standard);
  box-shadow: var(--shadow-deep);
  padding: var(--spacing-m) var(--spacing-l);
  font-size: 14px;
}

.el-message--success {
  background-color: #f0f9ff;
  color: var(--color-success);
  border-left: 4px solid var(--color-success);
}

.el-message--error {
  background-color: #fef0f0;
  color: var(--color-error);
  border-left: 4px solid var(--color-error);
}

.el-message--warning {
  background-color: #fdf6ec;
  color: var(--color-warning);
  border-left: 4px solid var(--color-warning);
}
```

### 13.2 Toast Notifications

```css
.el-notification {
  border-radius: var(--radius-standard);
  box-shadow: var(--shadow-deep);
  padding: var(--spacing-l);
  background-color: var(--color-bg-card);
}
```

### 13.3 Loading States

```css
.el-loading-mask {
  background-color: rgba(255, 255, 255, 0.9);
  z-index: 999;
}

.el-loading-spinner {
  width: 40px;
  height: 40px;
}
```

### 13.4 Empty States

```css
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-xl);
  color: var(--color-text-tertiary);
  text-align: center;
  min-height: 300px;
}

.empty-state__icon {
  font-size: 48px;
  margin-bottom: var(--spacing-m);
  color: var(--color-text-tertiary);
}

.empty-state__text {
  font-size: 14px;
  margin-bottom: var(--spacing-m);
}

.empty-state__button {
  margin-top: var(--spacing-m);
}
```

---

## 14. Animations and Interactions

### 14.1 Animation Duration System

| Level | Duration | Purpose |
|-------|----------|---------|
| Fast | 0.1s | Button click feedback |
| Standard | 0.3s | Page transitions, hover effects |
| Slow | 0.5s | Modal opening, complex animations |

### 14.2 Transition Animations

```css
/* Fade in/out */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Slide in */
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-enter-from {
  transform: translateX(-10px);
  opacity: 0;
}

.slide-leave-to {
  transform: translateX(10px);
  opacity: 0;
}

/* Scale in */
.scale-enter-active,
.scale-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0.95);
  opacity: 0;
}
```

### 14.3 Hover Effects

```css
/* Card hover */
.card {
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: var(--shadow-deep);
  transform: translateY(-2px);
}

/* Button hover */
.el-button {
  transition: all 0.3s ease;
}

/* Link hover */
a {
  transition: color 0.3s ease;
}

a:hover {
  color: #66b1ff;
}
```

---

## 15. Responsive Layout

### 15.1 Responsive Breakpoints

| Device Type | Width Range | CSS Variable | Use Case |
|-------------|-------------|--------------|----------|
| Mobile | ≤ 768px | `--breakpoint-mobile` | Portrait phone |
| Tablet | 769-1024px | `--breakpoint-tablet` | Tablets |
| Desktop | ≥ 1025px | `--breakpoint-desktop` | Desktop |

### 15.2 Media Queries

```css
:root {
  --breakpoint-mobile: 768px;
  --breakpoint-tablet: 1024px;
}

/* Mobile devices */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid var(--color-border);
  }

  .main-content {
    padding: var(--spacing-m);
  }

  .el-button {
    width: 100%;
    margin-bottom: var(--spacing-s);
  }

  h1 {
    font-size: 24px;
  }

  h2 {
    font-size: 20px;
  }
}

/* Tablets */
@media (min-width: 769px) and (max-width: 1024px) {
  .sidebar {
    width: 150px;
  }

  .card {
    padding: var(--spacing-m);
  }
}

/* Desktop */
@media (min-width: 1025px) {
  .sidebar {
    width: 200px;
  }

  .main-content {
    padding: var(--spacing-xl);
  }
}

/* Extra large screens */
@media (min-width: 1440px) {
  .main-container {
    max-width: 1400px;
    margin: 0 auto;
  }
}
```

---

## 16. Accessibility Guidelines

### 16.1 Focus States

```css
/* Keyboard navigation focus */
.el-button:focus-visible,
a:focus-visible,
input:focus-visible {
  outline: none;
  box-shadow: var(--shadow-focus);
}
```

### 16.2 Color Contrast Ratios

- Body text to background contrast ratio at least 4.5:1
- Large text to background contrast ratio at least 3:1
- Interactive elements to background contrast ratio at least 3:1

### 16.3 Text Alternatives

```css
/* Icon buttons need text labels */
.icon-button {
  position: relative;
}

.icon-button::after {
  content: attr(aria-label);
  position: absolute;
  white-space: nowrap;
  overflow: hidden;
  text-indent: -9999px;
}
```

---

## 17. Performance Optimization Recommendations

1. **Use CSS Variables**: Facilitates theme switching and global maintenance
2. **Avoid Deep Nesting**: Reduces CSS specificity conflicts
3. **Enable GPU Acceleration**: Use `transform` and `opacity` instead of `left`, `top`
4. **Reduce Reflow and Repaint**: Use properties like `will-change`, `contain`
5. **Use Lazy Loading**: Load images and components on demand
6. **Code Splitting**: Split CSS files by route or functionality

---

## 18. Future Enhancement Plans

- Dark mode support (`prefers-color-scheme` media query)
- RTL (right-to-left) language support
- Screen reader optimization
- Custom theme generator tool
- More state component style definitions