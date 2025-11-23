import { createPinia } from "pinia";

const pinia = createPinia();

pinia.use(({ options, store }) => {
  if (!options.persist) return;

  const storageKey = `pinia-${store.$id}`;
  const fromStorage = localStorage.getItem(storageKey);
  if (fromStorage) {
    try {
      store.$patch(JSON.parse(fromStorage));
    } catch (e) {
      console.warn("Failed to parse persisted state", e);
    }
  }

  store.$subscribe(
    (_mutation, state) => {
      localStorage.setItem(storageKey, JSON.stringify(state));
    },
    { detached: true }
  );
});

export default pinia;
