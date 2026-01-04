<template>
  <v-card rounded="xl" class="pa-4 soft-card" variant="flat">
    <div class="d-flex align-center justify-space-between">
      <div class="text-body-2 text-medium-emphasis">{{ title }}</div>

      <v-avatar :color="iconColor" variant="tonal" size="36">
        <v-icon>{{ icon }}</v-icon>
      </v-avatar>
    </div>

    <div class="d-flex align-center mt-2" style="gap: 8px">
      <div class="text-h5 font-weight-bold">
        <span v-if="isPercent">{{ value }}%</span>
        <span v-else>{{ value }}</span>
      </div>

      <div v-if="suffix" class="text-body-2 text-success">
        {{ suffix }}
      </div>
    </div>

    <div class="text-caption text-medium-emphasis mt-1">
      {{ subtitle }}
    </div>
  </v-card>
</template>

<script setup>
defineProps({
  title: { type: String, default: "" },
  value: { type: [String, Number], default: 0 },
  suffix: { type: String, default: "" },
  subtitle: { type: String, default: "" },
  icon: { type: String, default: "mdi-information-outline" },
  iconColor: { type: String, default: "primary" },
  isPercent: { type: Boolean, default: false },
});
</script>

<style scoped>
.soft-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
