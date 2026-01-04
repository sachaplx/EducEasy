<template>
  <v-container fluid class="dashboard">
    <!-- Header -->
    <div class="dashboard__inner" style="gap: 12px">
      <div>
        <div class="text-h4 font-weight-bold">Bonjour, {{ displayName }}</div>
        <div class="text-body-2 text-medium-emphasis">
          Voici un aperçu de votre réseau scolaire aujourd’hui
        </div>
      </div>

      <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
        {{ error }}
      </v-alert>

      <v-row dense>
        <!-- Left: Classes by school -->
        <v-col cols="12" md="8">
          <v-card rounded="xl" class="pa-4">
            <div
              class="d-flex flex-wrap align-center justify-space-between"
              style="gap: 12px"
            >
              <div>
                <div class="text-h6 font-weight-bold">Classes par école</div>
                <div class="text-body-2 text-medium-emphasis">
                  Gérez vos classes et suivez la progression
                </div>
              </div>

              <div
                class="d-flex align-center"
                style="gap: 8px; min-width: 320px"
              >
                <v-select
                  v-model="selectedEcoleId"
                  :items="ecoles"
                  item-title="nom"
                  item-value="id"
                  label="École"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  :loading="loading.schools"
                  @update:modelValue="loadClasses"
                />
                <v-tooltip text="Actualiser" location="top">
                  <template #activator="{ props }">
                    <v-btn
                      v-bind="props"
                      icon
                      variant="text"
                      :loading="loading.classes"
                      :disabled="!selectedEcoleId"
                      @click="loadClasses"
                      aria-label="Actualiser"
                    >
                      <v-icon>mdi-refresh</v-icon>
                    </v-btn>
                  </template>
                </v-tooltip>
              </div>
            </div>

            <div class="mt-4">
              <template v-if="!selectedEcoleId && !loading.schools">
                <div class="text-medium-emphasis py-6">
                  Sélectionnez une école pour afficher ses classes.
                </div>
              </template>

              <template v-else-if="loading.classes">
                <v-row dense>
                  <v-col v-for="i in 4" :key="i" cols="12" sm="6">
                    <v-skeleton-loader type="card" />
                  </v-col>
                </v-row>
              </template>

              <template v-else-if="selectedEcoleId && !classes.length">
                <div class="text-medium-emphasis py-6">
                  Aucune classe pour cette école.
                </div>
              </template>

              <template v-else>
                <div class="class-grid">
                  <ClassCards
                    v-for="c in classes"
                    :key="c.id"
                    :name="classLabel(c)"
                    :level="cycleLabel(c)"
                    :teacher-name="teacherLabel(c)"
                    :teacher-initials="
                      initials(c.teacherFirstName, c.teacherLastName)
                    "
                    :student-count="pupilsCount(c)"
                    :course-count="coursesCount(c)"
                    :color="pickColor(c)"
                    @click="openClass(c)"
                  />
                </div>

                <v-btn
                  class="mt-4 dashed-btn"
                  variant="text"
                  block
                  prepend-icon="mdi-chevron-down"
                  @click="goToAllClasses"
                >
                  Voir toutes les classes
                </v-btn>
              </template>
            </div>
          </v-card>

          <!-- Actions rapides -->
          <div class="mt-6">
            <div class="text-h6 font-weight-bold mb-3">Actions rapides</div>
            <v-row dense>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Ajouter un élève"
                  icon="mdi-account-plus"
                  color="primary"
                  @click="actionAddPupil"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Nouvelle classe"
                  icon="mdi-google-classroom"
                  color="deep-orange"
                  @click="actionAddClass"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Créer un rapport"
                  icon="mdi-file-document-outline"
                  color="grey-darken-2"
                  @click="actionCreateReport"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Planifier"
                  icon="mdi-calendar"
                  color="grey-darken-2"
                  @click="actionSchedule"
                />
              </v-col>
            </v-row>
          </div>
        </v-col>

        <!-- Right: Recent activity -->
        <v-col cols="12" md="4"
          ><PresenceCard
            class="mb-4"
            :value="presenceRateDisplay"
            subtitle="Cette semaine"
            :loading="pupil.presence.loading"
            :error="pupil.presence.error"
            @refresh="pupil.fetchPresenceRate()"
          />
          <v-card rounded="xl" class="pa-4">
            <div class="d-flex align-center justify-space-between mb-2">
              <div class="text-h6 font-weight-bold">Activité récente</div>
              <v-btn
                variant="text"
                size="small"
                color="primary"
                @click="goToActivity"
              >
                Tout voir
              </v-btn>
            </div>

            <v-list lines="two" density="comfortable">
              <v-list-item
                v-for="(a, idx) in recentActivity"
                :key="idx"
                class="rounded-lg mb-1"
              >
                <template #prepend>
                  <v-avatar :color="a.color" variant="tonal" size="34">
                    <v-icon>{{ a.icon }}</v-icon>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ a.title }}
                </v-list-item-title>
                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ a.subtitle }}
                </v-list-item-subtitle>

                <template #append>
                  <div class="text-caption text-medium-emphasis">
                    {{ a.when }}
                  </div>
                </template>
              </v-list-item>
            </v-list>

            <v-card
              rounded="xl"
              class="mt-4 pa-4"
              color="primary"
              variant="flat"
            >
              <div class="text-subtitle-2 font-weight-bold text-white mb-1">
                Conseil du jour
              </div>
              <div class="text-body-2 text-white" style="opacity: 0.95">
                Pensez à valider les absences de la semaine avant vendredi.
              </div>
            </v-card>
          </v-card>
        </v-col>
      </v-row>

      <!-- Dialog: pupils in class -->
      <v-dialog v-model="pupilsDialog" max-width="720">
        <v-card rounded="xl">
          <v-card-title class="d-flex align-center justify-space-between">
            <div class="d-flex flex-column">
              <span class="text-h6 font-weight-bold">{{
                classLabel(activeClass)
              }}</span>
              <span class="text-body-2 text-medium-emphasis">
                {{ teacherLabel(activeClass) }}
              </span>
            </div>

            <div class="d-flex align-center" style="gap: 6px">
              <v-btn
                icon
                variant="text"
                :loading="activeState.loading"
                @click="reloadPupils(activeClass?.id)"
                aria-label="Actualiser élèves"
              >
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                @click="pupilsDialog = false"
                aria-label="Fermer"
              >
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>

          <v-card-text>
            <v-text-field
              v-model="pupilQuery"
              label="Rechercher un élève"
              variant="outlined"
              density="comfortable"
              hide-details
              prepend-inner-icon="mdi-magnify"
              class="mb-3"
            />

            <div v-if="activeState.loading" class="py-8 d-flex justify-center">
              <v-progress-circular indeterminate />
            </div>

            <v-alert
              v-else-if="activeState.error"
              type="error"
              variant="tonal"
              class="mb-3"
            >
              {{ activeState.error }}
            </v-alert>

            <div
              v-else-if="!activeState.items.length"
              class="text-medium-emphasis py-8 text-center"
            >
              Aucun élève dans cette classe.
            </div>

            <v-list v-else lines="two" density="comfortable">
              <v-list-item
                v-for="p in pupilsFiltered"
                :key="p.id"
                class="rounded mb-1"
                @click="goToPupil(p)"
              >
                <template #prepend>
                  <v-avatar :color="avatarColor(p.gender)" size="32">
                    <span class="avatar-initials">{{
                      initials(p.prenom, p.nom)
                    }}</span>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ p.prenom }} {{ p.nom }}
                </v-list-item-title>

                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ classLabel(activeClass) }}
                </v-list-item-subtitle>

                <template #append>
                  <v-btn
                    icon
                    size="small"
                    variant="text"
                    @click.stop="goToPupil(p)"
                    aria-label="Ouvrir profil"
                  >
                    <v-icon>mdi-open-in-new</v-icon>
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-dialog>
    </div>
  </v-container>
</template>

<script setup>
import { computed, onMounted, reactive, ref, defineComponent } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { usePupilStore } from "../stores/pupil";
import { api } from "../services/api";
import KpiCard from "../components/dashboard/KpiCards.vue";
import QuickAction from "../components/dashboard/QuickActions.vue";
import ClassCards from "../components/dashboard/ClassCards.vue";
import PresenceCard from "../components/dashboard/PresenceCard.vue";

/* -----------------------------
  State
------------------------------ */
const router = useRouter();
const auth = useAuthStore();
const pupil = usePupilStore();

const error = ref("");

const ecoles = ref([]);
const classes = ref([]);
const selectedEcoleId = ref(null);
const me = ref(null);

const presence = ref({
  loading: false,
  error: null,
  rate: 0,
  from: null,
  to: null,
});

const loading = ref({ schools: false, classes: false });

const byClass = reactive({}); // cache pupils by classId

// Dialog pupils
const pupilsDialog = ref(false);
const activeClassId = ref(null);
const pupilQuery = ref("");

/* -----------------------------
  Computed
------------------------------ */
const displayName = computed(
  () => me.value?.firstName || me.value?.lastName || ""
);

const presenceRateDisplay = computed(() => {
  const r = pupil.presence.rate;
  return typeof r === "number" ? Math.round(r) : null;
});

const activeClass = computed(
  () =>
    classes.value.find((c) => String(c.id) === String(activeClassId.value)) ||
    null
);

function state(classId) {
  if (!classId)
    return { loading: false, loaded: false, error: null, items: [] };
  if (!byClass[classId])
    byClass[classId] = {
      loading: false,
      loaded: false,
      error: null,
      items: [],
    };
  return byClass[classId];
}

const activeState = computed(() => state(activeClassId.value));

const pupilsFiltered = computed(() => {
  const s = activeState.value;
  const q = (pupilQuery.value || "").trim().toLowerCase();
  if (!q) return s.items;
  return s.items.filter((p) => {
    const nom = (p.nom || "").toLowerCase();
    const prenom = (p.prenom || "").toLowerCase();
    return nom.includes(q) || prenom.includes(q);
  });
});

const kpis = computed(() => {
  // NOTE: présence & deltas = placeholders (à brancher)
  const totalClasses = classes.value.length || 0;

  // total enseignants: unique sur teacher name/id si dispo
  const teachers = new Set(
    classes.value
      .map((c) =>
        `${c.teacherFirstName || ""}|${c.teacherLastName || ""}`.trim()
      )
      .filter((x) => x !== "|")
  );

  // total élèves: si une classe a un count natif, on le prend, sinon on prend cache chargé, sinon 0
  const totalPupils = classes.value.reduce((sum, c) => sum + pupilsCount(c), 0);

  return {
    totalPupils: totalPupils || 0,
    totalClasses,
    totalTeachers: teachers.size || 0,
    presenceRate: presence.value.rate,
  };
});

/* -----------------------------
  Fetch
------------------------------ */
onMounted(async () => {
  await loadEcoles();
  await pupil.fetchPresenceRate();
  try {
    loading.value = true;
    const { data } = await api.get("/whoami");
    me.value = data;
  } catch (e) {
    error.value = "Impossible de charger votre profil.";
  } finally {
    loading.value = false;
  }
});

async function loadEcoles() {
  if (loading.value.schools) return;
  loading.value.schools = true;
  error.value = "";
  try {
    const { data } = await api.get("/schools/mine", {
      params: { _ts: Date.now() },
    });
    ecoles.value = Array.isArray(data) ? data : [];

    // auto-select first school if none selected
    if (!selectedEcoleId.value && ecoles.value.length) {
      selectedEcoleId.value = ecoles.value[0].id;
      await loadClasses();
    }
  } catch (e) {
    error.value = "Impossible de charger vos écoles.";
  } finally {
    loading.value.schools = false;
  }
}

async function loadClasses() {
  if (!selectedEcoleId.value) return;
  if (loading.value.classes) return;
  loading.value.classes = true;
  error.value = "";
  try {
    const { data } = await api.get(
      `/classrooms/${selectedEcoleId.value}/list`,
      {
        params: { _ts: Date.now() },
      }
    );
    classes.value = Array.isArray(data) ? data : [];
  } catch (e) {
    error.value = "Impossible de charger les classes.";
  } finally {
    loading.value.classes = false;
  }
}

async function loadPupils(classId) {
  const s = state(classId);
  if (s.loaded || s.loading) return;
  s.loading = true;
  s.error = null;
  try {
    const { data } = await api.get(`/classrooms/${classId}/list/pupils`, {
      params: { _ts: Date.now() },
    });
    s.items = Array.isArray(data)
      ? data
      : Array.isArray(data?.pupils)
      ? data.pupils
      : [];
    s.loaded = true;
  } catch (e) {
    s.error = "Chargement des élèves impossible.";
  } finally {
    s.loading = false;
  }
}

async function reloadPupils(classId) {
  if (!classId) return;
  const s = state(classId);
  if (s.loading) return;
  s.loading = true;
  s.error = null;
  try {
    const { data } = await api.get(`/classrooms/${classId}/list/pupils`, {
      params: { _ts: Date.now() },
    });
    s.items = Array.isArray(data)
      ? data
      : Array.isArray(data?.pupils)
      ? data.pupils
      : [];
    s.loaded = true;
  } catch (e) {
    s.error = "Actualisation des élèves impossible.";
  } finally {
    s.loading = false;
  }
}

/* -----------------------------
  UI actions
------------------------------ */
function openClass(c) {
  activeClassId.value = c.id;
  pupilsDialog.value = true;
  pupilQuery.value = "";
  loadPupils(c.id);
}

function pickColor(c) {
  const palette = ["blue", "purple", "orange", "green", "pink"];
  return palette[Math.abs(Number(c?.id || 0)) % palette.length];
}

function goToPupil(p) {
  router.push({ name: "pupil-profile", params: { id: p.id } });
}

function goToAllClasses() {
  // TODO: route si tu as une page liste de classes
  // router.push({ name: "classrooms" })
  // En attendant, on ne fait rien.
}

function goToActivity() {
  // TODO: route activité
}

/* Actions rapides (routes à adapter) */
function actionAddPupil() {
  // router.push({ name: "pupil-create" })
}
function actionAddClass() {
  // router.push({ name: "classroom-create" })
}
function actionCreateReport() {
  // router.push({ name: "reports" })
}
function actionSchedule() {
  // router.push({ name: "calendar" })
}

/* -----------------------------
  Helpers (labels/colors)
------------------------------ */
function classLabel(c) {
  if (!c) return "";
  return c.name || c.nom || `Classe #${c.id}`;
}

function teacherLabel(c) {
  if (!c) return "Enseignant non renseigné";
  const ln = (c.teacherLastName || "").trim();
  const fn = (c.teacherFirstName || "").trim();
  const full = `${ln ? ln.toUpperCase() : ""} ${fn}`.trim();
  return full || "Enseignant non renseigné";
}

function cycleLabel(c) {
  // Si tu as déjà cycle/level dans l'objet classe, branche ici.
  // Sinon on déduit grossièrement du nom.
  const n = (classLabel(c) || "").toUpperCase();
  if (n.includes("CP") || n.includes("CE1") || n.includes("CE2"))
    return "Cycle 2";
  if (n.includes("CM1") || n.includes("CM2")) return "Cycle 3";
  return "Cycle";
}

function initials(firstName, lastName) {
  const f = (firstName || "").trim().charAt(0).toUpperCase();
  const l = (lastName || "").trim().charAt(0).toUpperCase();
  return f + l || "—";
}

function avatarColor(gender) {
  if (String(gender).toUpperCase() === "GIRL") return "pink";
  if (String(gender).toUpperCase() === "BOY") return "blue";
  return "grey";
}

function accentVuetifyColor(c) {
  // Palette proche de la maquette
  const palette = ["primary", "deep-purple", "deep-orange", "green"];
  const idx = Math.abs(Number(c?.id || 0)) % palette.length;
  return palette[idx];
}

function accentColor(c) {
  // Couleur CSS pour la bordure gauche (dépend du thème Vuetify)
  const map = {
    primary: "rgb(var(--v-theme-primary))",
    "deep-purple": "rgb(var(--v-theme-deepPurple))",
    "deep-orange": "rgb(var(--v-theme-deepOrange))",
    green: "rgb(var(--v-theme-green))",
  };
  // fallback si tes tokens de thème ne sont pas tous définis :
  const vu = accentVuetifyColor(c);
  return map[vu] || "rgb(var(--v-theme-primary))";
}

function pupilsCount(c) {
  // Si backend fournit déjà un count, utilise-le :
  const direct = c?.pupilsCount ?? c?.pupilCount ?? c?.nbEleves;
  if (typeof direct === "number") return direct;

  // Sinon si on a déjà chargé les élèves :
  const s = byClass[c?.id];
  if (s?.loaded) return s.items.length;

  // Sinon placeholder :
  return 0;
}

function coursesCount(c) {
  // TODO endpoint cours/ressources
  return c?.coursesCount ?? c?.nbCours ?? 12; // 12 pour coller à la maquette (placeholder)
}

/* -----------------------------
  Activity (placeholder)
------------------------------ */
const recentActivity = ref([
  {
    icon: "mdi-check-circle",
    color: "green",
    title: "Rapport hebdomadaire généré",
    subtitle: "CE2 • École du Parc",
    when: "Il y a 2h",
  },
  {
    icon: "mdi-account-plus",
    color: "primary",
    title: "Nouvel élève inscrit",
    subtitle: "Marie Dupont • CP",
    when: "Il y a 4h",
  },
  {
    icon: "mdi-alert-circle",
    color: "deep-orange",
    title: "Absence signalée",
    subtitle: "3 élèves • CM1",
    when: "Aujourd’hui",
  },
  {
    icon: "mdi-clock-outline",
    color: "grey-darken-2",
    title: "Réunion planifiée",
    subtitle: "Conseil des enseignants",
    when: "Demain 14h",
  },
]);
</script>

<style scoped>
.class-card {
  border-left: 4px solid;
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease;
}
.class-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.06);
}

.avatar-initials {
  font-weight: 800;
  font-size: 12px;
}

.quick-action {
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease;
}
.quick-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.06);
}

/* bouton "voir toutes les classes" en style pointillé */
.dashed-btn {
  border: 1px dashed rgba(0, 0, 0, 0.15);
  border-radius: 14px;
}

.dashboard {
  padding: 32px 24px;
}

.dashboard__inner {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
