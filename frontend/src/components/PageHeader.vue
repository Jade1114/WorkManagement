<template>
  <section class="page-toolbar card">
    <div class="page-toolbar__copy">
      <p class="page-toolbar__eyebrow">{{ eyebrow }}</p>
      <h2>{{ title }}</h2>
      <p v-if="description" class="page-toolbar__description">{{ description }}</p>
    </div>

    <div class="page-toolbar__side">
      <div v-if="metricLabel || metricValue" class="page-toolbar__metric">
        <span>{{ metricLabel }}</span>
        <strong>{{ metricValue }}</strong>
      </div>
      <div v-if="$slots.actions" class="page-toolbar__actions">
        <slot name="actions" />
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: "Workspace" },
  title: { type: String, required: true },
  description: { type: String, default: "" },
  metricLabel: { type: String, default: "" },
  metricValue: { type: [String, Number], default: "" },
  variant: { type: String, default: "default" },
});
</script>

<style scoped>
.page-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: var(--spacing-l);
  align-items: center;
  min-height: 116px;
  padding: 18px;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--color-primary) 12%, transparent), transparent 48%),
    var(--color-bg-card);
}

.page-toolbar__copy {
  min-width: 0;
}

.page-toolbar__eyebrow {
  margin: 0 0 6px;
  color: var(--color-primary-strong);
  font-size: 12px;
  font-weight: 800;
}

.page-toolbar h2 {
  margin: 0;
}

.page-toolbar__description {
  max-width: 620px;
  margin: 8px 0 0;
  color: var(--color-text-secondary);
}

.page-toolbar__side {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.page-toolbar__metric {
  display: grid;
  min-width: 112px;
  gap: 2px;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  background: var(--color-bg-soft);
}

.page-toolbar__metric span {
  color: var(--color-text-tertiary);
  font-size: 12px;
  font-weight: 700;
}

.page-toolbar__metric strong {
  color: var(--color-text-primary);
  font-size: 24px;
  line-height: 1.1;
}

.page-toolbar__actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-s);
  justify-content: flex-end;
}

@media (max-width: 820px) {
  .page-toolbar {
    grid-template-columns: 1fr;
  }

  .page-toolbar__side {
    flex-wrap: wrap;
  }
}
</style>
