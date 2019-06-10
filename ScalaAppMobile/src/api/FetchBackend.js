const baseUrl = 'https://beerpass-scala.herokuapp.com';

const fetchBackend = async (url, options) => {
  try {
    const response = await fetch(url, options);
    const responseJson = await response.json();
    return responseJson;
  } catch (err) {
    console.error(err);
    return null;
  }
};

const fetchAllCompanies = async () => fetchBackend(`${baseUrl}/companies`);

const fetchAllBeers = async () => fetchBackend(`${baseUrl}/beers`);

const fetchCompanyDetails = async companyId => fetchBackend(`${baseUrl}/companies/${companyId}`);

const fetchUsedPasses = async userId => fetchBackend(`${baseUrl}/users/${userId}/offers/used`);

const fetchUnusedPasses = async userId => fetchBackend(`${baseUrl}/users/${userId}/offers/unused`);

const fetchMostPopularCompany = async () => fetchBackend(`${baseUrl}/stats/mostPopularCompany`);

const fetchMostFamousBeer = async () => fetchBackend(`${baseUrl}/stats/getMostFamousBeer`);

const fetchBeersForCompany = async companyId => fetchBackend(`${baseUrl}/companies/${companyId}/beers`);

const login = async (email, password,) => fetchBackend(`${baseUrl}/login`, {
  method: 'POST',
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email,
    password,
  }),
});

const useOffer = async (clientId, companyId, beerId) => fetchBackend(`${baseUrl}/useOffer`, {
  method: 'POST',
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    clientId,
    companyId,
    beerId
  }),
});

export default {
  fetchAllCompanies,
  fetchAllBeers,
  fetchCompanyDetails,
  fetchUsedPasses,
  fetchUnusedPasses,
  fetchMostPopularCompany,
  fetchMostFamousBeer,
  fetchBeersForCompany,
  login,
  useOffer
};
