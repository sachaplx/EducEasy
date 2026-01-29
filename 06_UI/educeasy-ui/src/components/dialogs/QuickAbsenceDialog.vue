<template>
  <v-dialog v-model="open" max-width="400">
    <v-card>
      <v-card-title class="d-flex align-center" style="gap: 8px">
        <v-icon color="warning">mdi-account-off</v-icon>
        <span>Marquer absent</span>
      </v-card-title>
      <v-card-text>
        <div class="text-body-2 mb-4">
          Marquer <strong>{{ pupils.length }}</strong> élève{{
            pupils.length > 1 ? "s" : ""
          }}
          absent{{ pupils.length > 1 ? "s" : "" }} :
        </div>

        <v-list density="compact" class="mb-4">
          <v-list-item
            v-for="pupil in pupils"
            :key="pupil.id"
            :title="`${pupil.prenom} ${pupil.nom}`"
          >
            <template #prepend>
              <v-avatar size="32" color="grey-lighten-2">
                <span class="text-caption">{{ getInitials(pupil) }}</span>
              </v-avatar>
            </template>
          </v-list-item>
        </v-list>

        <v-form ref="form" v-model="valid">
          <v-text-field
            v-model="displayDate"
            label="Date (jj/mm/aaaa)"
            placeholder="jj/mm/aaaa"
            variant="outlined"
            density="compact"
            hide-details="auto"
            class="mb-3"
            :rules="[r.required]"
          />

          <v-select
            v-model="formData.halfDay"
            :items="halfDays"
            label="Demi-journée"
            variant="outlined"
            density="compact"
            hide-details="auto"
            class="mb-3"
            :rules="[r.required]"
          />

          <v-checkbox
            v-model="formData.justifie"
            label="Absence justifiée"
            hide-details
            density="compact"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="close" :disabled="loading">Annuler</v-btn>
        <v-btn color="warning" :loading="loading" @click="submit">
          Marquer absent{{ pupils.length > 1 ? "s" : "" }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch, computed } from "vue";
import { api } from "@/services/api";
import { useToastStore } from "@/stores/toast";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pupils: { type: Array, default: () => [] },
});

const emit = defineEmits(["update:modelValue", "saved"]);
const toast = useToastStore();

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v),
);
watch(open, (v) => emit("update:modelValue", v));

const loading = ref(false);
const valid = ref(false);
const form = ref(null);

const halfDays = [
  { title: "Matin", value: "MORNING" },
  { title: "Après-midi", value: "AFTERNOON" },
  { title: "Journée", value: "FULL" },
];

const formData = ref({
  date: new Date().toISOString().slice(0, 10),
  halfDay: "FULL",
  justifie: false,
  motif: "",
});

const displayDate = computed({
  get() {
    if (!formData.value.date) return "";
    const [y, m, d] = formData.value.date.split("-");
    return `${d}/${m}/${y}`;
  },
  set(val) {
    if (!val) {
      formData.value.date = "";
      return;
    }
    const parts = val.split("/");
    if (parts.length === 3) {
      const [d, m, y] = parts;
      formData.value.date = `${y}-${m.padStart(2, "0")}-${d.padStart(2, "0")}`;
    }
  },
});

const r = {
  required: (v) => !!(v || v === 0) || "Requis",
};

function getInitials(pupil) {
  const first = (pupil.prenom || "").charAt(0) || "";
  const last = (pupil.nom || "").charAt(0) || "";
  return (first + last || "?").toUpperCase();
}

function close() {
  open.value = false;
}

async function submit() {
  const ok = await form.value?.validate();
  if (!ok?.valid) return;

  loading.value = true;
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const pupil of props.pupils) {
      try {
        await api.post(`/pupils/${pupil.id}/absences/add`, formData.value);
        successCount++;
      } catch (error) {
        console.error(`Error marking ${pupil.id} absent:`, error);
        errorCount++;
      }
    }

    if (successCount > 0) {
      toast.success(
        `${successCount} élève${successCount > 1 ? "s" : ""} marqué${successCount > 1 ? "s" : ""} absent${successCount > 1 ? "s" : ""}`,
      );
      emit("saved");
      close();
    }

    if (errorCount > 0) {
      toast.warning(`${errorCount} échec${errorCount > 1 ? "s" : ""}`);
    }
  } finally {
    loading.value = false;
  }
}
</script>
