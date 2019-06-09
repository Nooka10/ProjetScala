const baseUrl = 'https://beerpass-scala.herokuapp.com';

const fetchBackend = async (url) => {
  try {
    const response = await fetch(url);

    const responseJson = await response.json();

    console.log('responseJson', responseJson);

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


export default {
  fetchAllCompanies,
  fetchAllBeers,
  fetchCompanyDetails,
  fetchUsedPasses,
  fetchUnusedPasses,
  fetchMostPopularCompany,
  fetchMostFamousBeer,
  fetchBeersForCompany,

};
