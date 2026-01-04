<template>
  <v-card rounded="xl" class="pa-4 soft-card">
    <div class="d-flex align-center justify-space-between">
      <div>
        <div class="text-body-2 text-medium-emphasis">Taux de présence</div>
        <div class="text-h5 font-weight-bold mt-1">{{ value }}%</div>
      </div>

      <v-avatar color="primary" variant="tonal" size="40">
        <v-icon>mdi-trending-up</v-icon>
      </v-avatar>
    </div>

    <div class="text-caption text-medium-emphasis mt-2">
      {{ subtitle }}
    </div>

    <v-progress-linear class="mt-3" :model-value="value" height="8" rounded />
  </v-card>
</template>

<script setup>
defineProps({
  value: { type: Number, default: 0 },
  subtitle: { type: String, default: "Cette semaine" },
});
</script>

<style scoped>
.soft-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
