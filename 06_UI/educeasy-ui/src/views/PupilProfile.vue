<template>
  <v-container class="py-6">
    <v-breadcrumbs
      :items="[{ title: 'Accueil', to: '/' }, { title: 'Élève' }]"
      class="mb-4"
    />
    <v-card class="mb-4">
      <v-card-text
        class="d-flex align-center justify-space-between flex-wrap"
        style="gap: 12px"
      >
        <div class="d-flex align-center" style="gap: 12px">
          <v-avatar :color="avatarColor" size="44">
            <span class="avatar-initials text-h6">{{ initials }}</span>
          </v-avatar>
          <div>
            <div class="text-h6">{{ fullName }}</div>
            <div class="text-medium-emphasis">{{ gender }}</div>
          </div>
        </div>
        <div class="d-flex align-center flex-wrap" style="gap: 8px">
          <v-chip
            v-if="details?.classeNom"
            variant="tonal"
            color="primary"
            size="small"
            >{{ details.classeNom }}</v-chip
          >
          <v-chip v-if="details?.ecoleNom" variant="tonal" size="small">{{
            details.ecoleNom
          }}</v-chip>
        </div>
      </v-card-text>
    </v-card>

    <v-card>
      <v-tabs v-model="tab" bg-color="primary" density="comfortable">
        <v-tab value="infos" class="text-white">Infos</v-tab>
        <v-tab value="notes" class="text-white">Notes</v-tab>
        <v-tab value="absences" class="text-white">Absences</v-tab>
        <v-tab value="remarques" class="text-white">Remarques</v-tab>
      </v-tabs>

      <v-card-text>
        <v-window v-model="tab">
          <!-- ========== INFOS ========== -->
          <v-window-item value="infos">
            <div v-if="loading.details">
              <v-skeleton-loader type="article, actions" />
            </div>
            <div v-else>
              <v-row>
                <v-col cols="12" md="4">
                  <v-list density="compact">
                    <v-list-item
                      title="Nom"
                      :subtitle="details?.nom || 'N/C'"
                    />
                    <v-list-item
                      title="Prénom"
                      :subtitle="details?.prenom || 'N/C'"
                    />
                  </v-list>
                </v-col>
                <v-col cols="12" md="8">
                  <v-alert
                    v-if="!details?.classroom && !details?.school"
                    type="info"
                    variant="tonal"
                  >
                    Aucune inscription active (classe/école) pour cet élève.
                  </v-alert>
                  <div v-else class="text-medium-emphasis">
                    Actuellement en <b>{{ details?.classroom }}</b> —
                    {{ details?.school || "École N/C" }}
                  </div>
                </v-col>
              </v-row>
            </div>
          </v-window-item>

          <!-- ========== NOTES ========== -->
          <v-window-item value="notes">
            <div class="d-flex align-center flex-wrap" style="gap: 8px">
              <v-text-field
                v-model="filters.matiere"
                label="Matière"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 180px"
              />
              <v-text-field
                v-model="filters.notes.from"
                label="Du (YYYY-MM-DD)"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-text-field
                v-model="filters.notes.to"
                label="Au (YYYY-MM-DD)"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-btn color="primary" size="small" @click="loadNotes"
                >Filtrer</v-btn
              >
              <div class="ms-auto">
                <v-btn
                  size="small"
                  variant="tonal"
                  prepend-icon="mdi-note-plus"
                  @click="open.note = true"
                >
                  Ajouter une note
                </v-btn>
              </div>
            </div>

            <v-data-table
              class="mt-3"
              :loading="loading.notes"
              :items="notes"
              :headers="noteHeaders"
              :items-per-page="10"
            >
              <template #[`item.date`]="{ value }">
                {{ fmtDate(value) }}
              </template>

              <template #[`item.note`]="{ value }">
                <b>{{ value }}</b>
              </template>

              <!-- Actions: delete icon -->
              <template #[`item.actions`]="{ item }">
                <v-btn
                  icon
                  size="small"
                  variant="text"
                  color="red"
                  @click="askDelete('note', item.id)"
                  :aria-label="`Supprimer la note ${item.id}`"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>

              <template #no-data>
                <div class="text-medium-emphasis py-6">Aucune note.</div>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- ========== ABSENCES ========== -->
          <v-window-item value="absences">
            <div class="d-flex align-center flex-wrap" style="gap: 8px">
              <v-text-field
                v-model="filters.absences.from"
                label="Du (YYYY-MM-DD)"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-text-field
                v-model="filters.absences.to"
                label="Au (YYYY-MM-DD)"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-select
                v-model="filters.absences.halfDay"
                :items="halfDayItems"
                item-title="label"
                item-value="code"
                label="Demi-journée"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 180px"
                clearable
              />
              <v-select
                v-model="filters.absences.justifie"
                :items="[
                  { text: 'Toutes', value: null },
                  { text: 'Oui', value: true },
                  { text: 'Non', value: false },
                ]"
                item-title="text"
                item-value="value"
                label="Justifiée"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
                clearable
              />
              <v-btn color="primary" size="small" @click="loadAbsences"
                >Filtrer</v-btn
              >
              <div class="ms-auto">
                <v-btn
                  size="small"
                  variant="tonal"
                  prepend-icon="mdi-account-off"
                  @click="open.abs = true"
                  >Ajouter une absence</v-btn
                >
              </div>
            </div>

            <v-data-table
              class="mt-3"
              :loading="loading.absences"
              :items="absences"
              :headers="absHeaders"
              :items-per-page="10"
            >
              <template #[`item.date`]="{ value }">
                {{ fmtDate(value) }}
              </template>

              <template #[`item.halfDay`]="{ item }">
                {{ item.halfDayLabel || djLabel(item.halfDayCode) }}
              </template>

              <template #[`item.justifie`]="{ value }">
                <v-chip
                  :color="value ? 'success' : 'warning'"
                  size="small"
                  variant="tonal"
                >
                  {{ value ? "Oui" : "Non" }}
                </v-chip>
              </template>

              <!-- Actions: delete icon -->
              <template #[`item.actions`]="{ item }">
                <v-btn
                  icon
                  size="small"
                  variant="text"
                  color="red"
                  @click="askDelete('absence', item.id)"
                  :aria-label="`Supprimer l'absence ${item.id}`"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>

              <template #no-data>
                <div class="text-medium-emphasis py-6">Aucune absence.</div>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- ========== REMARQUES ========== -->
          <v-window-item value="remarques">
            <div class="d-flex justify-end mb-2">
              <v-btn
                size="small"
                variant="tonal"
                prepend-icon="mdi-comment-plus"
                @click="open.rem = true"
              >
                Ajouter une remarque
              </v-btn>
            </div>
            <div v-if="loading.remarks">
              <v-skeleton-loader
                type="list-item-two-line, list-item-two-line, list-item-two-line"
              />
            </div>

            <v-list v-else lines="two">
              <v-list-item
                v-for="r in remarks"
                :key="r.id"
                :title="`${fmtDateTime(r.createdAt)} • ${r.type || 'INFO'}`"
                :subtitle="`${
                  r.auteurLastName.toUpperCase() + ' ' + r.auteurFirstName ||
                  'Prof #' + r.authorId ||
                  'Auteur N/C'
                } — ${r.contenu}`"
              >
                <template #prepend>
                  <v-avatar size="28" color="primary" class="text-white">{{
                    (r.type || "I").charAt(0)
                  }}</v-avatar>
                </template>
                <template #append>
                  <v-btn
                    icon
                    size="small"
                    variant="text"
                    color="red"
                    @click="askDelete('remark', r.id)"
                    :aria-label="`Supprimer la remarque ${r.id}`"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-list-item>
              <div v-if="!remarks.length" class="text-medium-emphasis py-6">
                Aucune remarque.
              </div>
            </v-list>
          </v-window-item>
        </v-window>
      </v-card-text>
    </v-card>
    <v-card-actions class="justify-end"> </v-card-actions>

    <AddNoteDialog
      v-model="open.note"
      :pupil-id="pupilId"
      @saved="reloadNotes"
    />
    <AddAbsenceDialog
      v-model="open.abs"
      :pupil-id="pupilId"
      @saved="reloadAbsences"
    />
    <AddRemarkDialog
      v-model="open.rem"
      :pupil-id="pupilId"
      @saved="reloadRemarks"
    />

    <!-- Dialog de confirmation réutilisable -->
    <v-dialog v-model="confirm.show" max-width="430">
      <v-card>
        <v-card-title class="text-h6">{{ confirm.title }}</v-card-title>
        <v-card-text>{{ confirm.message }}</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="closeConfirm"
            :disabled="confirm.loading"
          >
            Annuler
          </v-btn>
          <v-btn
            color="red"
            variant="flat"
            :loading="confirm.loading"
            @click="confirmDelete"
          >
            <v-icon start>mdi-delete</v-icon>
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="error.open" color="error" timeout="4000">
      {{ error.msg }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { api } from "../services/api";
import AddNoteDialog from "../components/dialogs/AddNoteDialog.vue";
import AddAbsenceDialog from "../components/dialogs/AddAbsenceDialog.vue";
import AddRemarkDialog from "../components/dialogs/AddRemarkDialog.vue";

const route = useRoute();
const id = computed(() => route.params.id);
const gender = computed(() => {
  if (!details.value) return "N/C";
  const g = String(details.value.gender).toUpperCase();
  if (g === "GIRL") return "Fille";
  if (g === "BOY") return "Garçon";
  return "N/C";
});
const pupilId = computed(() => {
  const v = parseInt(route.params.id, 10);
  return isNaN(v) ? null : v;
});
const open = ref({ note: false, abs: false, rem: false });

// Etat du dialog de confirmation
const confirm = ref({
  show: false,
  loading: false,
  kind: /** @type {null | 'note' | 'absence' | 'remark'} */ (null),
  id: /** @type {null | number} */ (null),
  title: "",
  message: "",
});
function askDelete(kind, targetId) {
  confirm.value.kind = kind;
  confirm.value.id = targetId;
  confirm.value.title =
    kind === "note"
      ? "Supprimer cette note ?"
      : kind === "absence"
      ? "Supprimer cette absence ?"
      : "Supprimer cette remarque ?";
  confirm.value.message =
    "Cette action est définitive. Voulez-vous continuer ?";
  confirm.value.show = true;
}
function closeConfirm() {
  confirm.value.show = false;
  confirm.value.loading = false;
  confirm.value.kind = null;
  confirm.value.id = null;
}
async function confirmDelete() {
  if (!confirm.value.kind || !confirm.value.id) return;
  confirm.value.loading = true;
  try {
    if (confirm.value.kind === "note") {
      await api.delete(`/pupils/${id.value}/grades/${confirm.value.id}`);
      await loadNotes();
    } else if (confirm.value.kind === "absence") {
      await api.delete(`/pupils/${id.value}/absences/${confirm.value.id}`);
      await loadAbsences();
    } else if (confirm.value.kind === "remark") {
      await api.delete(`/pupils/${id.value}/remarks/${confirm.value.id}`);
      await loadRemarks();
    }
    closeConfirm();
  } catch (e) {
    showErr("La suppression a échoué.");
    confirm.value.loading = false;
  }
}

const tab = ref("infos");
const details = ref(null);
const notes = ref([]);
const absences = ref([]);
const remarks = ref([]);

const loading = ref({
  details: true,
  notes: false,
  absences: false,
  remarks: false,
});
const error = ref({ open: false, msg: "" });

const filters = ref({
  matiere: "",
  notes: { from: "", to: "" },
  absences: { from: "", to: "", halfDay: null, justifie: null },
});

const noteHeaders = [
  { title: "Date", key: "date" },
  { title: "Matière", key: "matiere" },
  { title: "Note", key: "note" },
  { title: "Commentaire", key: "commentaire" },
  { title: "", key: "actions", sortable: false, align: "end", width: 70 },
];
const absHeaders = [
  { title: "Date", key: "date" },
  { title: "Demi-journée", key: "halfDay" },
  { title: "Justifiée", key: "justifie" },
  { title: "Motif", key: "motif" },
  { title: "", key: "actions", sortable: false, align: "end", width: 70 },
];

const halfDayItems = [
  { code: 0, label: "Matin" },
  { code: 1, label: "Après-midi" },
  { code: 2, label: "Journée" },
];
function djLabel(code) {
  if (code === 0) return "Matin";
  if (code === 1) return "Après-midi";
  if (code === 2) return "Journée";
  return "N/C";
}

const fullName = computed(() =>
  details.value ? `${details.value.prenom} ${details.value.nom}` : "Élève"
);
const initials = computed(() => {
  if (!details.value) return "E";
  const a = (details.value.prenom || "").charAt(0) || "";
  const b = (details.value.nom || "").charAt(0) || "";
  return (a + b || "E").toUpperCase();
});

const avatarColor = computed(() => {
  if (!details.value) return "grey";
  const g = String(details.value.gender).toUpperCase();
  if (g === "GIRL") return "pink";
  if (g === "BOY") return "blue";
  return "grey";
});

function reloadNotes() {
  loadNotes();
}
function reloadAbsences() {
  loadAbsences();
}
function reloadRemarks() {
  loadRemarks();
}

function fmtDate(d) {
  try {
    return new Date(d).toLocaleDateString();
  } catch {
    return d;
  }
}
function fmtDateTime(d) {
  try {
    return new Date(d).toLocaleString();
  } catch {
    return d;
  }
}
function showErr(msg) {
  error.value = { open: true, msg };
}

async function loadDetails() {
  loading.value.details = true;
  try {
    const { data } = await api.get(`/pupils/${id.value}`);
    details.value = data;
  } catch (e) {
    showErr("Impossible de charger les infos élève.");
  } finally {
    loading.value.details = false;
  }
}

async function loadNotes() {
  loading.value.notes = true;
  try {
    const params = {};
    if (filters.value.matiere) params.matiere = filters.value.matiere;
    if (filters.value.notes.from) params.from = filters.value.notes.from;
    if (filters.value.notes.to) params.to = filters.value.notes.to;
    const { data } = await api.get(`/pupils/${id.value}/grades`, { params });
    notes.value = data;
  } catch (e) {
    showErr("Chargement des notes impossible.");
  } finally {
    loading.value.notes = false;
  }
}

async function loadAbsences() {
  loading.value.absences = true;
  try {
    const f = filters.value.absences;
    const params = {};
    if (f.from) params.from = f.from;
    if (f.to) params.to = f.to;
    if (f.halfDay !== null && f.halfDay !== undefined)
      params.halfDay = f.halfDay;
    if (f.justifie !== null && f.justifie !== undefined)
      params.justifie = f.justifie;
    const { data } = await api.get(`/pupils/${id.value}/absences`, { params });
    absences.value = data;
  } catch (e) {
    showErr("Chargement des absences impossible.");
  } finally {
    loading.value.absences = false;
  }
}

async function loadRemarks() {
  loading.value.remarks = true;
  try {
    const { data } = await api.get(`/pupils/${id.value}/remarks`);
    remarks.value = data;
  } catch (e) {
    showErr("Chargement des remarques impossible.");
  } finally {
    loading.value.remarks = false;
  }
}

async function loadAll() {
  await loadDetails();
  await Promise.all([loadNotes(), loadAbsences(), loadRemarks()]);
}

onMounted(loadAll);
watch(() => id.value, loadAll);
</script>
