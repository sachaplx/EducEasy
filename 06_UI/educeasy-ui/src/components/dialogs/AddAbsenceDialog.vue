<template>
  <v-dialog v-model="open" max-width="520">
    <v-card>
      <v-card-title>Ajouter une absence</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid" validate-on="submit">
          <v-text-field
            v-model="displayDate"
            label="Date (jj/mm/aaaa)"
            placeholder="jj/mm/aaaa"
            :rules="[r.required, r.validDate]"
          />
          <v-select
            v-model="formData.halfDay"
            :items="halfDays"
            label="Demi-journée"
            :rules="[r.required]"
          />
          <v-switch
            v-model="formData.justifie"
            label="Justifiée ?"
            color="primary"
            hide-details
          />
          <v-textarea
            v-model="formData.motif"
            label="Motif (optionnel)"
            rows="3"
            auto-grow
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="justify-end">
        <v-btn variant="text" @click="close">Annuler</v-btn>
        <v-btn color="primary" :loading="loading" @click="submit"
          >Enregistrer</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch, computed } from "vue";
import { api } from "@/services/api";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pupilId: { type: Number, required: true },
});
const emit = defineEmits(["update:modelValue", "saved"]);

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
  halfDay: "MORNING",
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
  validDate: (v) => {
    if (!v) return true;
    const regex = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
    const match = v.match(regex);
    if (!match) return "Format: jj/mm/aaaa";
    const [, d, m, y] = match;
    const date = new Date(y, m - 1, d);
    if (
      date.getDate() != d ||
      date.getMonth() != m - 1 ||
      date.getFullYear() != y
    ) {
      return "Date invalide";
    }
    return true;
  },
};

function close() {
  open.value = false;
}

function resetForm() {
  formData.value = {
    date: new Date().toISOString().slice(0, 10),
    halfDay: "MORNING",
    justifie: false,
    motif: "",
  };
}

async function submit() {
  const ok = await form.value?.validate();
  if (!ok?.valid) return;
  loading.value = true;
  try {
    await api.post(`/pupils/${props.pupilId}/absences/add`, formData.value);
    emit("saved");
    resetForm();
    close();
  } catch (error) {
    console.error("Error adding absence:", error);
  } finally {
    loading.value = false;
  }
}
</script>
