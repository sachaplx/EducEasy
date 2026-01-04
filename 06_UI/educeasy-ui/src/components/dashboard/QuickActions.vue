<template>
  <v-card
    rounded="xl"
    class="pa-4 soft-card quick-action"
    variant="outlined"
    role="button"
    tabindex="0"
    @click="$emit('click')"
    @keydown.enter="$emit('click')"
  >
    <v-avatar :color="color" variant="tonal" size="44" class="mb-3">
      <v-icon>{{ icon }}</v-icon>
    </v-avatar>

    <div class="text-body-2 font-weight-semibold">
      {{ title }}
    </div>
  </v-card>
</template>

<script setup>
defineEmits(["click"]);

defineProps({
  title: { type: String, default: "" },
  icon: { type: String, default: "mdi-flash" },
  color: { type: String, default: "primary" },
});
</script>

<style scoped>
.soft-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.quick-action {
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease;
}
.quick-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.06);
}
</style>
