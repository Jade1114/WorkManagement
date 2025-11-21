<template>
  <div class="app-shell">
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: var(--color-bg-page);
  padding: var(--spacing-xl);
}

.app-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}
</style>
