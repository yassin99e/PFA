from flask import Flask, jsonify
from flask_cors import CORS
import json
from datetime import datetime
from collections import Counter, defaultdict

app = Flask(__name__)
CORS(app)  # Enable CORS for Angular frontend


# Load test data
def load_test_data():
    with open('test_users.json', 'r') as f:
        users = json.load(f)

    with open('test_job_offers.json', 'r') as f:
        job_offers = json.load(f)

    with open('test_applications.json', 'r') as f:
        applications = json.load(f)

    return users, job_offers, applications


users, job_offers, applications = load_test_data()


@app.route('/api/analytics/admin/dashboard', methods=['GET'])
def admin_dashboard():
    """Admin Dashboard - Simple user counts"""
    try:
        # Count users by role
        role_counts = Counter(user['role'] for user in users)
        total_users = len(users)

        return jsonify({
            'totalUsers': total_users,
            'candidatesCount': role_counts['CANDIDATE'],
            'recruitersCount': role_counts['RECRUITER'],
            'userRoleChart': {
                'labels': ['Candidates', 'Recruiters'],
                'data': [role_counts['CANDIDATE'], role_counts['RECRUITER']]
            }
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/analytics/recruiter/<int:recruiter_id>/dashboard', methods=['GET'])
def recruiter_dashboard(recruiter_id):
    """Recruiter Dashboard - Job offers and score distribution"""
    try:
        # Get recruiter's job offers
        recruiter_jobs = [job for job in job_offers if job['recruiterId'] == recruiter_id]

        # Get applications for recruiter's jobs
        recruiter_job_ids = [job['id'] for job in recruiter_jobs]
        recruiter_applications = [app for app in applications if app['jobOfferId'] in recruiter_job_ids]

        # Score distribution
        score_ranges = {
            '0-20': 0,
            '21-40': 0,
            '41-60': 0,
            '61-80': 0,
            '81-100': 0
        }

        for app in recruiter_applications:
            score = app['matchScore']
            if score <= 20:
                score_ranges['0-20'] += 1
            elif score <= 40:
                score_ranges['21-40'] += 1
            elif score <= 60:
                score_ranges['41-60'] += 1
            elif score <= 80:
                score_ranges['61-80'] += 1
            else:
                score_ranges['81-100'] += 1

        # Job offers with application count
        jobs_with_stats = []
        for job in recruiter_jobs:
            job_applications = [app for app in applications if app['jobOfferId'] == job['id']]
            jobs_with_stats.append({
                'id': job['id'],
                'title': job['title'],
                'applicationsCount': len(job_applications)
            })

        return jsonify({
            'jobOffers': jobs_with_stats,
            'totalApplications': len(recruiter_applications),
            'scoreDistribution': {
                'labels': list(score_ranges.keys()),
                'data': list(score_ranges.values())
            }
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/analytics/candidate/dashboard', methods=['GET'])
def candidate_dashboard():
    """Candidate Dashboard - Trending job titles and technologies"""
    try:
        # Most demanded job titles (count occurrences)
        job_title_counts = Counter(job['title'] for job in job_offers)

        # Most trending technologies (flatten and count)
        all_technologies = []
        for job in job_offers:
            all_technologies.extend(job['requiredTechnologies'])

        tech_counts = Counter(all_technologies)

        # Get top 10 for each
        top_job_titles = dict(job_title_counts.most_common(10))
        top_technologies = dict(tech_counts.most_common(10))

        return jsonify({
            'trendingJobTitles': {
                'labels': list(top_job_titles.keys()),
                'data': list(top_job_titles.values())
            },
            'trendingTechnologies': {
                'labels': list(top_technologies.keys()),
                'data': list(top_technologies.values())
            }
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({'status': 'healthy', 'service': 'Analytics Service'})


@app.route('/', methods=['GET'])
def home():
    """Home endpoint"""
    return jsonify({
        'service': 'Analytics Service',
        'version': '1.0.0',
        'endpoints': [
            '/api/analytics/admin/dashboard',
            '/api/analytics/recruiter/<id>/dashboard',
            '/api/analytics/candidate/dashboard',
            '/health'
        ]
    })


if __name__ == '__main__':
    print("Starting Analytics Service on http://localhost:8084")
    print("Available endpoints:")
    print("- GET /api/analytics/admin/dashboard")
    print("- GET /api/analytics/recruiter/<id>/dashboard")
    print("- GET /api/analytics/candidate/dashboard")
    print("- GET /health")

    app.run(host='0.0.0.0', port=8084, debug=True)